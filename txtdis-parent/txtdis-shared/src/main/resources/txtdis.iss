[Setup]
AppId={#MyAppName}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
AppVerName={#MyAppVersionName}
AppPublisher={#MyAppPublisher}
AppComments={#MyAppName}
AppCopyright=Copyright (C) 2013 {#MyAppPublisher}
DefaultDirName={localappdata}\{#MyApp}
DisableStartupPrompt=Yes
DisableDirPage=Yes
DisableProgramGroupPage=Yes
DisableReadyPage=Yes
DisableFinishedPage=Yes
DisableWelcomePage=Yes
DefaultGroupName={#MyApp}

MinVersion=0,5.1 
OutputBaseFilename={#MyAppVersionName}
Compression=lzma
SolidCompression=yes
PrivilegesRequired=lowest
SetupIconFile={#MyResourceDir}\{#MyIconName}.ico
UninstallDisplayIcon={app}\{#MyAppType}\{#MyAppExeName}
UninstallDisplayName={#MyAppName}
WizardImageStretch=No
WizardSmallImageFile={#MyResourceDir}\{#MyIconName}.bmp
ArchitecturesInstallIn64BitMode=x64

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Files]
Source: "{#MyTargetDir}\{#MyAppExeName}"; DestDir: "{app}\{#MyAppType}"; Flags: ignoreversion
Source: "{#MyTargetDir}\{#MyApp}-bin\{#MyApp}\lib\{#MyJars}"; DestDir: "{app}\{#MyAppType}\lib"; Flags: ignoreversion
Source: "{#MyKeyStoreDir}\{#MyKeyStore}.p12"; DestDir: "{app}\{#MyAppType}"; Flags: ignoreversion 

[Icons]
Name: "{group}\{#MyAppName}"; Filename: "{app}\{#MyAppType}\{#MyAppExeName}"; 
Name: "{commondesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppType}\{#MyAppExeName}"; 

[InstallDelete]
Type: files; Name: "{app}\{#MyAppType}\lib\txtdis*.*"

[Run]
Filename: "{app}\{#MyAppType}\{#MyAppExeName}"; WorkingDir: "{app}\{#MyAppType}"; Flags: nowait postinstall
