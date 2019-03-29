//
//  InstallableLexicalModel.swift
//  TestProperListSetToUserDefaults
//
//  Created by Randy Boring on 3/28/19.
//  Copyright © 2019 Randy Boring. All rights reserved.
//


import Foundation

/// Mainly differs from the API `LexicalModel` by having an associated language.
public struct InstallableLexicalModel: Codable {
    public var id: String
    public var name: String
    public var languageID: String
    public var languageName: String
    public var version: String
    public var isCustom: Bool
    
    public init(id: String,
                name: String,
                languageID: String,
                languageName: String,
                version: String,
                isCustom: Bool) {
        self.id = id
        self.name = name
        self.languageID = languageID
        self.languageName = languageName
        self.version = version
        self.isCustom = isCustom
    }
}
