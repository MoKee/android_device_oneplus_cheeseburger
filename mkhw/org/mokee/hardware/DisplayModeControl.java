/*
 * Copyright (C) 2015 The CyanogenMod Project
 * Copyright (C) 2015 The MoKee Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mokee.hardware;

import mokee.hardware.DisplayMode;

import org.mokee.internal.util.FileUtils;

/*
 * Display Modes API
 *
 * A device may implement a list of preset display modes for different
 * viewing intents, such as movies, photos, or extra vibrance. These
 * modes may have multiple components such as gamma correction, white
 * point adjustment, etc, but are activated by a single control point.
 *
 * This API provides support for enumerating and selecting the
 * modes supported by the hardware.
 */

public class DisplayModeControl {

    private static final String PROFILE_PATH
            = "/sys/class/graphics/fb0/color_profile";

    private static final String LOCAL_MODE_ID
            = "/data/misc/display/livedisplay_mode";

    private static final DisplayMode MODE_NONE
            = new DisplayMode(0, "basic");

    private static final DisplayMode MODE_SRGB
            = new DisplayMode(1, "srgb");

    private static final DisplayMode MODE_ADOBE_RGB
            = new DisplayMode(2, "adobergb");

    private static final DisplayMode MODE_DCI_P3
            = new DisplayMode(3, "dcip3");

    private static final DisplayMode[] MODES = new DisplayMode[] {
        MODE_NONE,
        MODE_SRGB,
        MODE_ADOBE_RGB,
        MODE_DCI_P3,
    };

    /*
     * All HAF classes should export this boolean.
     * Real implementations must, of course, return true
     */
    public static boolean isSupported() {
        return FileUtils.isFileWritable(PROFILE_PATH);
    }

    /*
     * Get the list of available modes. A mode has an integer
     * identifier and a string name.
     *
     * It is the responsibility of the upper layers to
     * map the name to a human-readable format or perform translation.
     */
    public static DisplayMode[] getAvailableModes() {
        return MODES;
    }

    /*
     * Get the name of the currently selected mode. This can return
     * null if no mode is selected.
     */
    public static DisplayMode getCurrentMode() {
        String line = FileUtils.readOneLine(LOCAL_MODE_ID);
        if (line == null) {
            return null;
        }

        int mode = Integer.parseInt(line);
        for (DisplayMode item : MODES) {
            if (item.id == mode) {
                return item;
            }
        }

        return null;
    }

    /*
     * Selects a mode from the list of available modes by it's
     * string identifier. Returns true on success, false for
     * failure. It is up to the implementation to determine
     * if this mode is valid.
     */
    public static boolean setMode(DisplayMode mode, boolean makeDefault) {
        for (DisplayMode item : MODES) {
            if (item.name.equals(mode.name)) {
                String value = String.valueOf(item.id);
                return FileUtils.writeLine(PROFILE_PATH, value) &&
                    (!makeDefault || FileUtils.writeLine(LOCAL_MODE_ID, value));
            }
        }

        return false;
    }

    /*
     * Gets the preferred default mode for this device by it's
     * string identifier. Can return null if there is no default.
     */
    public static DisplayMode getDefaultMode() {
        return MODE_NONE;
    }
}
