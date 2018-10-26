﻿program keyman;



uses
  Forms,
  Dialogs,
  UfrmKeyman7Main in 'UfrmKeyman7Main.pas' {frmKeyman7Main},
  RegistryKeys in '..\..\global\delphi\general\RegistryKeys.pas',
  IntegerList in '..\..\global\delphi\general\IntegerList.pas',
  custinterfaces in '..\..\global\delphi\cust\custinterfaces.pas',
  exceptionw in '..\..\global\delphi\general\exceptionw.pas',
  Unicode in '..\..\global\delphi\general\Unicode.pas',
  GetOsVersion in '..\..\global\delphi\general\GetOsVersion.pas',
  CRC32 in '..\..\global\delphi\general\CRC32.pas',
  KeyNames in '..\..\global\delphi\general\KeyNames.pas',
  MessageIdentifiers in '..\..\global\delphi\cust\MessageIdentifiers.pas',
  kmint in 'kmint.pas',
  BitmapIPicture in '..\..\global\delphi\general\BitmapIPicture.pas',
  klog in '..\..\global\delphi\general\klog.pas',
  VersionInfo in '..\..\global\delphi\general\VersionInfo.pas',
  keymanapi_TLB in '..\kmcomapi\keymanapi_TLB.pas',
  VisualKeyboardInfo in '..\..\global\delphi\visualkeyboard\VisualKeyboardInfo.pas',
  VisualKeyboardExportHTML in '..\..\global\delphi\visualkeyboard\VisualKeyboardExportHTML.pas',
  VisualKeyboard in '..\..\global\delphi\visualkeyboard\VisualKeyboard.pas',
  ScanCodeMap in '..\..\global\delphi\general\ScanCodeMap.pas',
  utildir in '..\..\global\delphi\general\utildir.pas',
  main in 'main.pas',
  KeymanTrayIcon in 'KeymanTrayIcon.pas',
  KeymanMenuItem in 'KeymanMenuItem.pas',
  utilhotkey in '..\..\global\delphi\general\utilhotkey.pas',
  InterfaceHotkeys in '..\..\global\delphi\general\InterfaceHotkeys.pas',
  ExtShiftState in '..\..\global\delphi\comp\ExtShiftState.pas',
  CleartypeDrawCharacter in '..\..\global\delphi\general\CleartypeDrawCharacter.pas',
  MLang in '..\..\global\delphi\general\MLang.pas',
  OnScreenKeyboard in '..\..\global\delphi\comp\OnScreenKeyboard.pas',
  UfrmOSKPlugInBase in 'viskbd\UfrmOSKPlugInBase.pas' {frmOSKPlugInBase},
  UfrmOSKCharacterMap in 'viskbd\UfrmOSKCharacterMap.pas' {frmOSKCharacterMap},
  UfrmOSKEntryHelper in 'viskbd\UfrmOSKEntryHelper.pas' {frmOSKEntryHelper},
  TTInfo in '..\..\global\delphi\general\TTInfo.pas',
  UnicodeData in '..\..\global\delphi\charmap\UnicodeData.pas',
  CharacterMapSettings in '..\..\global\delphi\charmap\CharacterMapSettings.pas',
  CharacterRanges in '..\..\global\delphi\charmap\CharacterRanges.pas',
  CharMapInsertMode in '..\..\global\delphi\charmap\CharMapInsertMode.pas',
  UfrmCharacterMapNew in '..\..\global\delphi\charmap\UfrmCharacterMapNew.pas' {frmCharacterMapNew},
  ADOX_TLB in '..\..\global\delphi\tlb\ADOX_TLB.pas',
  ADODB_TLB in '..\..\global\delphi\tlb\ADODB_TLB.pas',
  UfrmUnicodeDataStatus in '..\..\global\delphi\charmap\UfrmUnicodeDataStatus.pas' {frmUnicodeDataStatus},
  utilsystem in '..\..\global\delphi\general\utilsystem.pas',
  utilstr in '..\..\global\delphi\general\utilstr.pas',
  CharacterDragObject in '..\..\global\delphi\charmap\CharacterDragObject.pas',
  UfrmCharacterMapFilter in '..\..\global\delphi\charmap\UfrmCharacterMapFilter.pas' {frmCharacterMapFilter},
  FixedTrackbar in '..\..\global\delphi\comp\FixedTrackbar.pas',
  Menu_KeyboardItems in 'Menu_KeyboardItems.pas',
  Keyman.System.DebugLogManager in '..\..\global\delphi\debug\Keyman.System.DebugLogManager.pas',
  PaintPanel in '..\..\global\delphi\comp\PaintPanel.pas',
  exImageList in '..\..\global\delphi\comp\exImageList.pas',
  utilhttp in '..\..\global\delphi\general\utilhttp.pas',
  VistaMessages in 'VistaMessages.pas',
  MessageIdentifierConsts in '..\..\global\delphi\cust\MessageIdentifierConsts.pas',
  VisualKeyboardExportXML in '..\..\global\delphi\visualkeyboard\VisualKeyboardExportXML.pas',
  VKeys in '..\..\global\delphi\general\VKeys.pas',
  DebugPaths in '..\..\global\delphi\general\DebugPaths.pas',
  VisualKeyboardParameters in '..\..\global\delphi\visualkeyboard\VisualKeyboardParameters.pas',
  utilxml in '..\..\global\delphi\general\utilxml.pas',
  MSHTML_TLB in '..\..\global\delphi\tlb\MSHTML_TLB.pas',
  MSXML2_TLB in '..\..\global\delphi\tlb\MSXML2_TLB.pas',
  ExternalExceptionHandler in '..\..\global\delphi\general\ExternalExceptionHandler.pas',
  DCPcrypt2 in '..\..\global\delphi\crypt\DCPcrypt2.pas',
  DCPbase64 in '..\..\global\delphi\crypt\DCPbase64.pas',
  DCPconst in '..\..\global\delphi\crypt\DCPconst.pas',
  DCPrc4 in '..\..\global\delphi\crypt\Ciphers\DCPrc4.pas',
  KeymanControlMessages in '..\..\global\delphi\general\KeymanControlMessages.pas',
  UfrmOSKFontHelper in 'viskbd\UfrmOSKFontHelper.pas' {frmOSKFontHelper},
  HintConsts in '..\..\global\delphi\hints\HintConsts.pas',
  KeymanHints in 'KeymanHints.pas',
  UfrmKeymanBase in '..\..\global\delphi\ui\UfrmKeymanBase.pas' {frmKeymanBase: TTntForm},
  UfrmWebContainer in '..\..\global\delphi\ui\UfrmWebContainer.pas' {frmWebContainer: TTntForm},
  XMLRenderer in '..\..\global\delphi\ui\XMLRenderer.pas',
  UfrmHint in '..\..\global\delphi\hints\UfrmHint.pas' {frmHint},
  GenericXMLRenderer in '..\..\global\delphi\ui\GenericXMLRenderer.pas',
  Hints in '..\..\global\delphi\hints\Hints.pas',
  UfrmHelp in 'UfrmHelp.pas' {frmHelp: TTntForm},
  UfrmOSKKeyboardUsage in 'viskbd\UfrmOSKKeyboardUsage.pas' {frmOSKKeyboardUsage},
  utilcheckfonts in '..\..\global\delphi\general\utilcheckfonts.pas',
  findfonts in '..\..\global\delphi\general\findfonts.pas',
  WideStringClass in '..\..\global\delphi\general\WideStringClass.pas',
  WebBrowserManager in '..\..\global\delphi\comp\WebBrowserManager.pas',
  ErrLogPath in '..\..\global\delphi\general\ErrLogPath.pas',
  UFixupMissingFile in '..\..\global\delphi\ui\UFixupMissingFile.pas',
  utiluac in '..\..\global\delphi\general\utiluac.pas',
  WebSoundControl in '..\..\global\delphi\general\WebSoundControl.pas',
  VKeyChars in '..\..\global\delphi\general\VKeyChars.pas',
  usp10 in '..\..\global\delphi\general\usp10.pas',
  UserMessages in '..\..\global\delphi\general\UserMessages.pas',
  UfrmKeymanMenu in 'UfrmKeymanMenu.pas' {frmKeymanMenu},
  UILanguages in '..\..\desktop\kmshell\util\UILanguages.pas',
  LangSwitchManager in 'langswitch\LangSwitchManager.pas',
  LoadIndirectStringUnit in 'langswitch\LoadIndirectStringUnit.pas',
  UfrmLanguageSwitch in 'langswitch\UfrmLanguageSwitch.pas' {frmLanguageSwitch},
  Glossary in '..\..\global\delphi\general\Glossary.pas',
  OnlineConstants in '..\..\global\delphi\productactivation\OnlineConstants.pas',
  LayeredFormUtils in '..\..\global\delphi\general\LayeredFormUtils.pas',
  UfrmOSKOnScreenKeyboard in 'viskbd\UfrmOSKOnScreenKeyboard.pas' {frmOSKOnScreenKeyboard},
  KeymanEmbeddedWB in '..\..\global\delphi\comp\KeymanEmbeddedWB.pas',
  ErrorControlledRegistry in '..\..\global\delphi\vcl\ErrorControlledRegistry.pas',
  UfrmVisualKeyboard in 'viskbd\UfrmVisualKeyboard.pas' {frmVisualKeyboard},
  utilexecute in '..\..\global\delphi\general\utilexecute.pas',
  KeymanVersion in '..\..\global\delphi\general\KeymanVersion.pas',
  MSXMLDomCreate in '..\..\global\delphi\general\MSXMLDomCreate.pas',
  UfrmScriptError in '..\..\global\delphi\general\UfrmScriptError.pas' {frmScriptError},
  OnScreenKeyboardData in '..\..\global\delphi\visualkeyboard\OnScreenKeyboardData.pas',
  Keyman.System.Util.RenderLanguageIcon in '..\..\global\delphi\ui\Keyman.System.Util.RenderLanguageIcon.pas',
  TempFileManager in '..\..\global\delphi\general\TempFileManager.pas',
  SHDocVw in '..\..\global\delphi\vcl\SHDocVw.pas',
  utiltsf in '..\..\global\delphi\general\utiltsf.pas',
  GlobalKeyboardChangeManager in 'GlobalKeyboardChangeManager.pas',
  utilwow64 in '..\..\global\delphi\general\utilwow64.pas',
  USendInputString in '..\..\global\delphi\general\USendInputString.pas',
  Upload_Settings in '..\..\global\delphi\general\Upload_Settings.pas',
  keyman_msctf in '..\..\global\delphi\winapi\keyman_msctf.pas',
  KeymanDesktopShell in 'KeymanDesktopShell.pas',
  KeymanPaths in '..\..\global\delphi\general\KeymanPaths.pas',
  utiljclexception in '..\..\global\delphi\general\utiljclexception.pas',
  StockFileNames in '..\..\global\delphi\cust\StockFileNames.pas',
  KeymanEngineControl in '..\..\global\delphi\general\KeymanEngineControl.pas',
  VisualKeyboardLoaderBinary in '..\..\global\delphi\visualkeyboard\VisualKeyboardLoaderBinary.pas',
  VisualKeyboardLoaderXML in '..\..\global\delphi\visualkeyboard\VisualKeyboardLoaderXML.pas',
  VisualKeyboardSaverBinary in '..\..\global\delphi\visualkeyboard\VisualKeyboardSaverBinary.pas',
  VisualKeyboardSaverXML in '..\..\global\delphi\visualkeyboard\VisualKeyboardSaverXML.pas',
  Windows8LanguageList in '..\..\global\delphi\general\Windows8LanguageList.pas',
  BCP47Tag in '..\..\global\delphi\general\BCP47Tag.pas',
  Keyman.System.LanguageCodeUtils in '..\..\global\delphi\general\Keyman.System.LanguageCodeUtils.pas',
  Keyman.System.Standards.ISO6393ToBCP47Registry in '..\..\global\delphi\standards\Keyman.System.Standards.ISO6393ToBCP47Registry.pas',
  Keyman.System.Standards.LCIDToBCP47Registry in '..\..\global\delphi\standards\Keyman.System.Standards.LCIDToBCP47Registry.pas',
  kmxfileconsts in '..\..\global\delphi\general\kmxfileconsts.pas',
  Keyman.System.Standards.BCP47SubtagRegistry in '..\..\global\delphi\standards\Keyman.System.Standards.BCP47SubtagRegistry.pas',
  Keyman.System.Standards.BCP47SuppressScriptRegistry in '..\..\global\delphi\standards\Keyman.System.Standards.BCP47SuppressScriptRegistry.pas',
  Keyman.System.Standards.LibPalasoAllTagsRegistry in '..\..\global\delphi\standards\Keyman.System.Standards.LibPalasoAllTagsRegistry.pas',
  Keyman.System.CanonicalLanguageCodeUtils in '..\..\global\delphi\general\Keyman.System.CanonicalLanguageCodeUtils.pas',
  Keyman.System.DebugLogClient in '..\..\global\delphi\debug\Keyman.System.DebugLogClient.pas',
  Keyman.System.DebugLogCommon in '..\..\global\delphi\debug\Keyman.System.DebugLogCommon.pas';

{$R ICONS.RES}
{$R VERSION.RES}
{$R MANIFEST.RES}

begin
  //InitTntEnvironment;
  //ShowMessage('Start');
  Run;
end.
