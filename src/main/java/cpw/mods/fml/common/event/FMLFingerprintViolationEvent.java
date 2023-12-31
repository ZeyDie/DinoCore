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

package cpw.mods.fml.common.event;

import com.google.common.collect.ImmutableSet;

import java.io.File;
import java.util.Set;


public class FMLFingerprintViolationEvent extends FMLEvent {

    public final boolean isDirectory;
    public final Set<String> fingerprints;
    public final File source;
    public final String expectedFingerprint;

    public FMLFingerprintViolationEvent(final boolean isDirectory, final File source, final ImmutableSet<String> fingerprints, final String expectedFingerprint)
    {
        super();
        this.isDirectory = isDirectory;
        this.source = source;
        this.fingerprints = fingerprints;
        this.expectedFingerprint = expectedFingerprint;
    }
}
