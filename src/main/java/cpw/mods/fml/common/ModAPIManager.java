package cpw.mods.fml.common;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import cpw.mods.fml.common.asm.transformers.ModAPITransformer;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.discovery.ASMDataTable.ASMData;
import cpw.mods.fml.common.discovery.ModCandidate;
import cpw.mods.fml.common.discovery.ModDiscoverer;
import cpw.mods.fml.common.functions.ModIdFunction;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import cpw.mods.fml.common.versioning.VersionParser;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ModAPIManager {
    public static final ModAPIManager INSTANCE = new ModAPIManager();
    private ModAPITransformer transformer;
    private ASMDataTable dataTable;
    private Map<String,APIContainer> apiContainers;

    private static class APIContainer extends DummyModContainer {
        private List<ArtifactVersion> referredMods;
        private ArtifactVersion ownerMod;
        private ArtifactVersion ourVersion;
        private String providedAPI;
        private File source;
        private String version;
        private Set<String> currentReferents;
        private Set<String> packages;

        public APIContainer(final String providedAPI, final String apiVersion, final File source, final ArtifactVersion ownerMod)
        {
            this.providedAPI = providedAPI;
            this.version = apiVersion;
            this.ownerMod = ownerMod;
            this.ourVersion = new DefaultArtifactVersion(providedAPI, apiVersion);
            this.referredMods = Lists.newArrayList();
            this.source = source;
            this.currentReferents = Sets.newHashSet();
            this.packages = Sets.newHashSet();
        }

        @Override
        public File getSource()
        {
            return source;
        }
        @Override
        public String getVersion()
        {
            return version;
        }
        @Override
        public String getName()
        {
            return "API: "+providedAPI;
        }
        @Override
        public String getModId()
        {
            return "API:"+providedAPI;
        }
        @Override
        public List<ArtifactVersion> getDependants()
        {
            return referredMods;
        }

        @Override
        public List<ArtifactVersion> getDependencies()
        {
            return ImmutableList.of(ownerMod);
        }

        @Override
        public ArtifactVersion getProcessedVersion()
        {
            return ourVersion;
        }

        public void validate(final String providedAPI, final String apiOwner, final String apiVersion)
        {
            // TODO Compare this annotation data to the one we first found. Maybe barf if there is inconsistency?
        }

        @Override
        public String toString()
        {
            return "APIContainer{"+providedAPI+":"+version+"}";
        }

        public void addAPIReference(final String embedded)
        {
            if (currentReferents.add(embedded))
            {
                referredMods.add(VersionParser.parseVersionReference(embedded));
            }
        }

        public void addOwnedPackage(final String apiPackage)
        {
            packages.add(apiPackage);
        }

        public void addAPIReferences(final List<String> candidateIds)
        {
            for (final String modId : candidateIds)
            {
                addAPIReference(modId);
            }
        }
    }
    public void registerDataTableAndParseAPI(final ASMDataTable dataTable)
    {
        this.dataTable = dataTable;

        final Set<ASMData> apiList = dataTable.getAll("cpw.mods.fml.common.API");

        apiContainers = Maps.newHashMap();

        for (final ASMData data : apiList)
        {
            final Map<String, Object> annotationInfo = data.getAnnotationInfo();
            final String apiPackage = data.getClassName().substring(0,data.getClassName().indexOf(".package-info"));
            final String providedAPI = (String) annotationInfo.get("provides");
            final String apiOwner = (String) annotationInfo.get("owner");
            final String apiVersion = (String) annotationInfo.get("apiVersion");
            APIContainer container = apiContainers.get(providedAPI);
            if (container == null)
            {
                container = new APIContainer(providedAPI, apiVersion, data.getCandidate().getModContainer(), VersionParser.parseVersionReference(apiOwner));
                apiContainers.put(providedAPI, container);
            }
            else
            {
                container.validate(providedAPI, apiOwner, apiVersion);
            }
            container.addOwnedPackage(apiPackage);
            for (final ModContainer mc : data.getCandidate().getContainedMods())
            {
                final String embeddedIn = mc.getModId();
                if (container.currentReferents.contains(embeddedIn))
                {
                    continue;
                }
                FMLLog.fine("Found API %s (owned by %s providing %s) embedded in %s",apiPackage, apiOwner, providedAPI, embeddedIn);
                if (!embeddedIn.equals(apiOwner))
                {
                    container.addAPIReference(embeddedIn);
                }
            }
        }

        for (final APIContainer container : apiContainers.values())
        {
            for (final String pkg : container.packages)
            {
                final Set<ModCandidate> candidates = dataTable.getCandidatesFor(pkg);
                for (final ModCandidate candidate : candidates)
                {
                    final List<String> candidateIds = Lists.transform(candidate.getContainedMods(), new ModIdFunction());
                    if (!candidateIds.contains(container.ownerMod.getLabel()) && !container.currentReferents.containsAll(candidateIds))
                    {
                        FMLLog.info("Found mod(s) %s containing declared API package %s (owned by %s) without associated API reference",candidateIds, pkg, container.ownerMod);
                        container.addAPIReferences(candidateIds);
                    }
                }
            }
            if (apiContainers.containsKey(container.ownerMod.getLabel()))
            {
                ArtifactVersion owner = container.ownerMod;
                do
                {
                    final APIContainer parent = apiContainers.get(owner.getLabel());
                    FMLLog.finest("Removing upstream parent %s from %s", parent.ownerMod.getLabel(), container);
                    container.currentReferents.remove(parent.ownerMod.getLabel());
                    container.referredMods.remove(parent.ownerMod);
                    owner = parent.ownerMod;
                }
                while (apiContainers.containsKey(owner.getLabel()));
            }
            FMLLog.fine("Creating API container dummy for API %s: owner: %s, dependents: %s", container.providedAPI, container.ownerMod, container.referredMods);
        }
    }

    public void manageAPI(final ModClassLoader modClassLoader, final ModDiscoverer discoverer)
    {
        registerDataTableAndParseAPI(discoverer.getASMTable());
        transformer = modClassLoader.addModAPITransformer(dataTable);
    }

    public void injectAPIModContainers(final List<ModContainer> mods, final Map<String, ModContainer> nameLookup)
    {
        mods.addAll(apiContainers.values());
        nameLookup.putAll(apiContainers);
    }

    public void cleanupAPIContainers(final List<ModContainer> mods)
    {
        mods.removeAll(apiContainers.values());
    }

    public boolean hasAPI(final String modId)
    {
        return apiContainers.containsKey(modId);
    }
}
