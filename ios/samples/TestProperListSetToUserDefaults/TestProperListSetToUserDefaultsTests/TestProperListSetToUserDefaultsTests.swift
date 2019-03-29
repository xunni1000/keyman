//
//  TestProperListSetToUserDefaultsTests.swift
//  TestProperListSetToUserDefaultsTests
//
//  Created by Randy Boring on 3/28/19.
//  Copyright © 2019 Randy Boring. All rights reserved.
//

import XCTest
@testable import TestProperListSetToUserDefaults

class TestProperListSetToUserDefaultsTests: XCTestCase {

    override func setUp() {
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }

    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
    }

    func testLM() {
        // This is an example of a functional test case.
        // Use XCTAssert and related functions to verify your tests produce the correct results.
        let instLM = InstallableLexicalModel(id: "a",
                                             name: "b",
                                             languageID: "c",
                                             languageName: "c",
                                             version: "1.0",
                                             isCustom: true)
        let userDefaults: UserDefaults = UserDefaults.standard
        userDefaults.userLexicalModels = [instLM]
        XCTAssertTrue(true)
    }
    
    func testKbd() {
        // This is an example of a functional test case.
        // Use XCTAssert and related functions to verify your tests produce the correct results.
        let instKbd = InstallableKeyboard(id: "a",
                                             name: "b",
                                             languageID: "c",
                                             languageName: "c",
                                             version: "1.0",
                                             isRTL: false,
                                             isCustom: true)
        let userDefaults: UserDefaults = UserDefaults.standard
        userDefaults.userKeyboards = [instKbd]
        XCTAssertTrue(true)
    }

    func testPerformanceExample() {
        // This is an example of a performance test case.
        self.measure {
            // Put the code you want to measure the time of here.
        }
    }

}
