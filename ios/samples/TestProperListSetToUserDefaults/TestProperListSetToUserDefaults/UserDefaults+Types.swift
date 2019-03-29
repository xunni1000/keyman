//
//  UserDefaults+Types.swift
//  TestProperListSetToUserDefaults
//
//  Created by Randy Boring on 3/28/19.
//  Copyright © 2019 Randy Boring. All rights reserved.
//
// based on
//
//  UserDefaults+Types.swift
//  KeymanEngine
//
//  Created by Gabriel Wong on 2017-10-26.
//  Copyright © 2017 SIL International. All rights reserved.
//

import Foundation

public extension UserDefaults {
    public func installableKeyboards(forKey key: String) -> [InstallableKeyboard]? {
        guard let array = array(forKey: key) as? [Data] else {
            return nil
        }
        let decoder = PropertyListDecoder()
        do {
            return try array.map { try decoder.decode(InstallableKeyboard.self, from: $0) }
        } catch {
            return nil
        }
    }
    public func installableLexicalModels(forKey key: String) -> [InstallableLexicalModel]? {
        guard let array = array(forKey: key) as? [Data] else {
            return nil
        }
        let decoder = PropertyListDecoder()
        do {
            return try array.map { try decoder.decode(InstallableLexicalModel.self, from: $0) }
        } catch {
            return nil
        }
    }
    
    public func set(_ keyboards: [InstallableKeyboard]?, forKey key: String) {
        guard let keyboards = keyboards else {
            removeObject(forKey: key)
            return
        }
        let encoder = PropertyListEncoder()
        do {
            let array = try keyboards.map { try encoder.encode($0) }
            set(array, forKey: key)
        } catch {
        }
    }
    
    public var userKeyboards: [InstallableKeyboard]? {
        get {
            return installableKeyboards(forKey: Key.userKeyboardsList)
        }
        
        set(keyboards) {
            set(keyboards, forKey: Key.userKeyboardsList)
        }
    }
    
    public var userLexicalModels: [InstallableLexicalModel]? {
        get {
            return installableLexicalModels(forKey: Key.userLexicalModelsList)
        }
        
        set(lexicalModels) {
            set(lexicalModels, forKey: Key.userLexicalModelsList)
        }
    }
    
    var migrationLevel: Int {
        get {
            return integer(forKey: Key.migrationLevel)
        }
        
        set(level) {
            set(level, forKey: Key.migrationLevel)
        }
    }
}
