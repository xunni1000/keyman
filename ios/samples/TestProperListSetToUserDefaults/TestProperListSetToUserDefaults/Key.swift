//
//  Key.swift
//  TestProperListSetToUserDefaults
//
//  Created by Randy Boring on 3/28/19.
//  Copyright © 2019 Randy Boring. All rights reserved.
//
// based on
//
//  Constants.swift
//  KeymanEngine
//
//  Created by Gabriel Wong on 2017-10-20.
//  Copyright © 2017 SIL International. All rights reserved.
//

import Foundation

public enum Key {
    public static let keyboardInfo = "keyboardInfo"
    public static let lexicalModelInfo = "lexicalModelInfo"
    
    /// Array of user keyboards info list in UserDefaults
    static let userKeyboardsList = "UserKeyboardsList"
    
    /// Array of user keyboards info list in UserDefaults
    static let userLexicalModelsList = "UserLexicalModelsList"
    
    /// Currently/last selected keyboard info in UserDefaults
    static let userCurrentKeyboard = "UserCurrentKeyboard"
    
    /// Currently/last selected lexical model info in UserDefaults
    static let userCurrentLexicalModel = "UserCurrentLexicalModel"
    
    // Internal user defaults keys
    static let engineVersion = "KeymanEngineVersion"
    static let keyboardPickerDisplayed = "KeyboardPickerDisplayed"
    static let synchronizeSWKeyboard = "KeymanSynchronizeSWKeyboard"
    static let synchronizeSWLexicalModel = "KeymanSynchronizeSWLexicalModel"
    
    static let migrationLevel = "KeymanEngineMigrationLevel"
    
    // JSON keys for language REST calls
    static let options = "options"
    static let language = "language"
    
    // TODO: Check if it matches with the key in Keyman Cloud API
    static let keyboardCopyright = "copyright"
    static let lexicalModelCopyright = "lexicalmodelcopyright"
    static let languages = "languages"
    
    // Other keys
    static let update = "update"
}

public enum Defaults {
    public static let keyboard = InstallableKeyboard(id: "sil_euro_latin",
                                                     name: "EuroLatin (SIL)",
                                                     languageID: "en",
                                                     languageName: "English",
                                                     version: "1.8.1",
                                                     isRTL: false,
                                                     isCustom: false)
}

public enum Resources {
    /// Keyman Web resources
    public static let bundle: Bundle = {
        // If we're executing this code, KMEI's framework should already be loaded.  Just use that.
        let frameworkBundle = Bundle() //Bundle(identifier: "org.sil.Keyman.ios.Engine")!
        return Bundle(path: frameworkBundle.path(forResource: "Keyman", ofType: "bundle")!)!
    }()
    
    public static let oskFontFilename = "keymanweb-osk.ttf"
    static let kmwFilename = "keyboard.html"
}

public enum Util {
    /// Is the process of a custom keyboard extension. Avoid using this
    /// in most situations as Manager.shared.isSystemKeyboard is more
    /// reliable in situations where in-app and system keyboard can
    /// be used in the same app, for example using the Web Browser in
    /// the Keyman app. However, in initialization scenarios this test
    /// makes sense.
    public static let isSystemKeyboard: Bool = {
        let infoDict = Bundle.main.infoDictionary
        let extensionInfo = infoDict?["NSExtension"] as? [AnyHashable: Any]
        let extensionID = extensionInfo?["NSExtensionPointIdentifier"] as? String
        return extensionID == "com.apple.keyboard-service"
    }()
    
    /// The version of the Keyman SDK
    public static let sdkVersion: String = {
        let url = Resources.bundle.url(forResource: "KeymanEngine-Info", withExtension: "plist")!
        let info = NSDictionary(contentsOf: url)!
        return info["CFBundleVersion"] as! String
    }()
}

public enum FileExtensions {
    public static let javaScript = "js"
    public static let trueTypeFont = "ttf"
    public static let openTypeFont = "otf"
    public static let configurationProfile = "mobileconfig"
}
