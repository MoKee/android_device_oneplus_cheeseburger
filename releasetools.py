# Copyright (C) 2009 The Android Open Source Project
# Copyright (c) 2011, The Linux Foundation. All rights reserved.
# Copyright (C) 2017 The LineageOS Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import re

def FullOTA_Assertions(info):
  AddModemAssertion(info)
  AddFileEncryptionAssertion(info)
  return

def IncrementalOTA_Assertions(info):
  AddModemAssertion(info)
  AddFileEncryptionAssertion(info)
  return

def AddModemAssertion(info):
  android_info = info.input_zip.read("OTA/android-info.txt")
  m = re.search(r'require\s+version-modem\s*=\s*(.+)', android_info)
  if m:
    version = m.group(1).rstrip()
    if len(version) and '*' not in version:
      cmd = 'assert(oneplus.verify_modem("' + version + '") == "1");'
      info.script.AppendExtra(cmd)
  return

def AddFileEncryptionAssertion(info):
  info.script.AppendExtra('package_extract_file("install/bin/fbe_check.sh", "/tmp/fbe_check.sh");');
  info.script.AppendExtra('set_metadata("/tmp/fbe_check.sh", "uid", 0, "gid", 0, "mode", 0755);');
  info.script.AppendExtra('if !is_mounted("/data") then');
  info.script.Mount("/data");
  info.script.AppendExtra('endif;');
  info.script.AppendExtra('if run_program("/tmp/fbe_check.sh") != 0 then');
  info.script.AppendExtra('ui_print("===========================================");');
  info.script.AppendExtra('ui_print("|              !!! ERROR !!!              |");');
  info.script.AppendExtra('ui_print("|                                         |");');
  info.script.AppendExtra('ui_print("| File-based Encryption is required.      |");');
  info.script.AppendExtra('ui_print("|                                         |");');
  info.script.AppendExtra('ui_print("| Backup your data (including internal    |");');
  info.script.AppendExtra('ui_print("| storage) and format the data partition. |");');
  info.script.AppendExtra('ui_print("|                                         |");');
  info.script.AppendExtra('ui_print("| Chinese: http://t.cn/R92kLm3            |");');
  info.script.AppendExtra('ui_print("| English: https://t.co/7jDlsf2y6v        |");');
  info.script.AppendExtra('ui_print("===========================================");');
  info.script.AppendExtra('abort("FBE check failed.");');
  info.script.AppendExtra('endif;');
  info.script.Unmount("/data");
