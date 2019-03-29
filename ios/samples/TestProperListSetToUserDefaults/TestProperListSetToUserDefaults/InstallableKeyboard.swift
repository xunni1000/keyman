//
//  InstallableKeyboard.swift
//  TestProperListSetToUserDefaults
//
//  Created by Randy Boring on 3/28/19.
//  Copyright © 2019 Randy Boring. All rights reserved.
//
// based on
//
//  InstallableKeyboard.swift
//  KeymanEngine
//
//  Created by Gabriel Wong on 2017-10-24.
//  Copyright © 2017 SIL International. All rights reserved.
//

import Foundation

/// Mainly differs from the API `Keyboard` by having an associated language.
public struct InstallableKeyboard: Codable {
    public var id: String
    public var name: String
    public var languageID: String
    public var languageName: String
    public var version: String
    public var isRTL: Bool
    public var isCustom: Bool
    
    public init(id: String,
                name: String,
                languageID: String,
                languageName: String,
                version: String,
                isRTL: Bool,
                isCustom: Bool) {
        self.id = id
        self.name = name
        self.languageID = languageID
        self.languageName = languageName
        self.version = version
        self.isRTL = isRTL
        self.isCustom = isCustom
    }

}
