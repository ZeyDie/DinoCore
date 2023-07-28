/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
 */

package cpw.mods.fml.common.discovery;

import com.google.common.base.Predicate;
import com.google.common.collect.*;
import cpw.mods.fml.common.ModContainer;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ASMDataTable
{
    public final static class ASMData implements Cloneable
    {
        private ModCandidate candidate;
        private String annotationName;
        private String className;
        private String objectName;
        private Map<String,Object> annotationInfo;
        public ASMData(final ModCandidate candidate, final String annotationName, final String className, final String objectName, final Map<String,Object> info)
        {
            this.candidate = candidate;
            this.annotationName = annotationName;
            this.className = className;
            this.objectName = objectName;
            this.annotationInfo = info;
        }
        public ModCandidate getCandidate()
        {
            return candidate;
        }
        public String getAnnotationName()
        {
            return annotationName;
        }
        public String getClassName()
        {
            return className;
        }
        public String getObjectName()
        {
            return objectName;
        }
        public Map<String, Object> getAnnotationInfo()
        {
            return annotationInfo;
        }

        public ASMData copy(final Map<String,Object> newAnnotationInfo)
        {
            try
            {
                final ASMData clone = (ASMData) this.clone();
                clone.annotationInfo = newAnnotationInfo;
                return clone;
            }
            catch (final CloneNotSupportedException e)
            {
                throw new RuntimeException("Unpossible", e);
            }
        }
    }

    private static class ModContainerPredicate implements Predicate<ASMData>
    {
        private ModContainer container;
        public ModContainerPredicate(final ModContainer container)
        {
            this.container = container;
        }
        public boolean apply(final ASMData data)
        {
            return container.getSource().equals(data.candidate.getModContainer());
        }
    }
    private SetMultimap<String, ASMData> globalAnnotationData = HashMultimap.create();
    private Map<ModContainer, SetMultimap<String,ASMData>> containerAnnotationData;

    private List<ModContainer> containers = Lists.newArrayList();
    private SetMultimap<String,ModCandidate> packageMap = HashMultimap.create();

    public SetMultimap<String,ASMData> getAnnotationsFor(final ModContainer container)
    {
        if (containerAnnotationData == null)
        {
            final ImmutableMap.Builder<ModContainer, SetMultimap<String, ASMData>> mapBuilder = ImmutableMap.<ModContainer, SetMultimap<String,ASMData>>builder();
            for (final ModContainer cont : containers)
            {
                final Multimap<String, ASMData> values = Multimaps.filterValues(globalAnnotationData, new ModContainerPredicate(cont));
                mapBuilder.put(cont, ImmutableSetMultimap.copyOf(values));
            }
            containerAnnotationData = mapBuilder.build();
        }
        return containerAnnotationData.get(container);
    }

    public Set<ASMData> getAll(final String annotation)
    {
        return globalAnnotationData.get(annotation);
    }

    public void addASMData(final ModCandidate candidate, final String annotation, final String className, final String objectName, final Map<String,Object> annotationInfo)
    {
        globalAnnotationData.put(annotation, new ASMData(candidate, annotation, className, objectName, annotationInfo));
    }

    public void addContainer(final ModContainer container)
    {
        this.containers.add(container);
    }

    public void registerPackage(final ModCandidate modCandidate, final String pkg)
    {
        this.packageMap.put(pkg,modCandidate);
    }

    public Set<ModCandidate> getCandidatesFor(final String pkg)
    {
        return this.packageMap.get(pkg);
    }
}
