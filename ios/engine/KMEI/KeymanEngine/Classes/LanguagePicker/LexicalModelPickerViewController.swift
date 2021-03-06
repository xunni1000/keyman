//
//  LexicalModelPickerViewController.swift
//  KeymanEngine
//
//  Created by Randy Boring on 3/19/19.
//  Copyright © 2019 SIL International. All rights reserved.
//

import Foundation
import UIKit

private let toolbarButtonTag = 100
private let toolbarLabelTag = 101
private let toolbarActivityIndicatorTag = 102

class LexicalModelPickerViewController: UITableViewController, UIAlertViewDelegate {
  private var userLexicalModels: [InstallableLexicalModel] = [InstallableLexicalModel]()
  private var updateQueue: [InstallableLexicalModel]?
  private var _isDoneButtonEnabled = false
  private var isDidUpdateCheck = false
  
  private var lexicalModelDownloadStartedObserver: NotificationObserver?
  private var lexicalModelDownloadCompletedObserver: NotificationObserver?
  private var lexicalModelDownloadFailedObserver: NotificationObserver?
  
  override func viewDidLoad() {
    super.viewDidLoad()
    
    title = "LexicalModels"
    setIsDoneButtonEnabled(false)
    isDidUpdateCheck = false
    updateQueue = nil
    if Manager.shared.canAddNewLexicalModels {
      let addButton = UIBarButtonItem(barButtonSystemItem: .add, target: self,
                                      action: #selector(self.addClicked))
      navigationItem.rightBarButtonItem = addButton
    }
    
    navigationController?.toolbar?.barTintColor = UIColor(red: 0.5, green: 0.75,
                                                          blue: 0.25, alpha: 0.9)
    
    lexicalModelDownloadStartedObserver = NotificationCenter.default.addObserver(
      forName: Notifications.lexicalModelDownloadStarted,
      observer: self,
      function: LexicalModelPickerViewController.lexicalModelDownloadStarted)
    lexicalModelDownloadCompletedObserver = NotificationCenter.default.addObserver(
      forName: Notifications.lexicalModelDownloadCompleted,
      observer: self,
      function: LexicalModelPickerViewController.lexicalModelDownloadCompleted)
    lexicalModelDownloadFailedObserver = NotificationCenter.default.addObserver(
      forName: Notifications.lexicalModelDownloadFailed,
      observer: self,
      function: LexicalModelPickerViewController.lexicalModelDownloadFailed)
  }
  
  override func viewWillAppear(_ animated: Bool) {
    super.viewWillAppear(animated)
    
    loadUserLexicalModels()
    scroll(toSelectedLexicalModel: false)
  }
  
  override func viewDidAppear(_ animated: Bool) {
    super.viewDidAppear(animated)
    if isDidUpdateCheck || !checkUpdates() {
      return
    }
    
    let toolbarFrame = navigationController!.toolbar!.frame
    let button = UIButton(type: .roundedRect)
    button.addTarget(self, action: #selector(self.updateClicked), for: .touchUpInside)
    
    button.frame = CGRect(x: toolbarFrame.origin.x, y: toolbarFrame.origin.y,
                          width: toolbarFrame.width * 0.95, height: toolbarFrame.height * 0.7)
    button.center = CGPoint(x: toolbarFrame.width / 2, y: toolbarFrame.height / 2)
    button.tintColor = UIColor(red: 0.75, green: 1.0, blue: 0.5, alpha: 1.0)
    button.setTitleColor(UIColor.white, for: .normal)
    button.setTitle("Update available", for: .normal)
    button.autoresizingMask = [.flexibleLeftMargin, .flexibleRightMargin, .flexibleTopMargin,
                               .flexibleBottomMargin, .flexibleWidth, .flexibleHeight]
    button.tag = toolbarButtonTag
    navigationController?.toolbar?.addSubview(button)
    
    navigationController?.setToolbarHidden(false, animated: true)
    scroll(toSelectedLexicalModel: false)
  }
  
  override func numberOfSections(in tableView: UITableView) -> Int {
    return 1
  }
  
  override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
    return userLexicalModels.count
  }
  
  override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
    let cellIdentifier = "Cell"
    if let cell = tableView.dequeueReusableCell(withIdentifier: cellIdentifier) {
      return cell
    }
    
    let cell = UITableViewCell(style: .subtitle, reuseIdentifier: cellIdentifier)
    let selectionColor = UIView()
    selectionColor.backgroundColor = UIColor(red: 204.0 / 255.0, green: 136.0 / 255.0, blue: 34.0 / 255.0, alpha: 1.0)
    cell.selectedBackgroundView = selectionColor
    return cell
  }
  
  // TODO: Refactor. Duplicated in LexicalModelInfoViewController
  override func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
    if !Manager.shared.canRemoveLexicalModels {
      return false
    }
    
    if !Manager.shared.canRemoveDefaultLexicalModel {
      return indexPath.row != 0
    }
    if indexPath.row > 0 {
      return true
    }
    return userLexicalModels.count > 1
  }
  
  override func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCellEditingStyle,
                          forRowAt indexPath: IndexPath) {
    if editingStyle != .delete {
      return
    }
    
    if Manager.shared.removeLexicalModel(at: indexPath.row) {
      loadUserLexicalModels()
    }
    setIsDoneButtonEnabled(true)
  }
  
  override func tableView(_ tableView: UITableView,
                          accessoryButtonTappedForRowWith indexPath: IndexPath) {
    showLexicalModelInfoView(with: indexPath.row)
  }
  
  func showLexicalModelInfoView(with index: Int) {
    setIsDoneButtonEnabled(true)
    let lm = userLexicalModels[index]
    let version = lm.version
    
    let infoView = LexicalModelInfoViewController()
    infoView.title = lm.name
    infoView.lexicalModelCount = userLexicalModels.count
    infoView.lexicalModelIndex = index
    infoView.lexicalModelID = lm.id
    infoView.languageID = lm.languageID
    infoView.lexicalModelVersion = version
    infoView.isCustomLexicalModel = lm.isCustom
    navigationController?.pushViewController(infoView, animated: true)
  }
  
  override func tableView(_ tableView: UITableView,
                          willDisplay cell: UITableViewCell,
                          forRowAt indexPath: IndexPath) {
    cell.selectionStyle = .none
    let lm = userLexicalModels[indexPath.row]
    
    cell.textLabel?.text = lm.languageID // maybe do a lookup for lm.languageName
    cell.detailTextLabel?.text = lm.name
    cell.tag = indexPath.row
    
    if Manager.shared.currentLexicalModelID == lm.fullID {
      cell.selectionStyle = .blue
      cell.isSelected = true
      cell.accessoryType = .detailDisclosureButton
    } else {
      cell.selectionStyle = .none
      cell.isSelected = false
      cell.accessoryType = .detailDisclosureButton
    }
  }
  
  override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
    switchLexicalModel(indexPath.row)
  }
  
  private func lexicalModelDownloadStarted(_ lexicalModels: [InstallableLexicalModel]) {
    view.isUserInteractionEnabled = false
    navigationItem.leftBarButtonItem?.isEnabled = false
    navigationItem.rightBarButtonItem?.isEnabled = false
  }
  
  private func lexicalModelDownloadCompleted(_ lexicalModels: [InstallableLexicalModel]) {
    if view == navigationController?.topViewController?.view {
      if updateQueue == nil {
        return
      }
      Manager.shared.shouldReloadLexicalModel = true
      
      // Update lexicalModel version
      for lexicalModel in lexicalModels {
        Manager.shared.updateUserLexicalModels(with: lexicalModel)
      }
      
      updateQueue!.remove(at: 0)
      if !updateQueue!.isEmpty {
        let langID = updateQueue![0].languageID
        let lmID = updateQueue![0].id
        Manager.shared.downloadLexicalModel(withID: lmID, languageID: langID, isUpdate: true)
      } else {
        loadUserLexicalModels()
        view.isUserInteractionEnabled = true
        navigationItem.leftBarButtonItem?.isEnabled = true
        if navigationItem.rightBarButtonItem != nil {
          navigationItem.rightBarButtonItem?.isEnabled = true
        }
        updateQueue = nil
        let label = navigationController?.toolbar?.viewWithTag(toolbarLabelTag) as? UILabel
        label?.text = "Lexical models successfully updated!"
        navigationController?.toolbar?.viewWithTag(toolbarActivityIndicatorTag)?.removeFromSuperview()
        Timer.scheduledTimer(timeInterval: 3.0, target: self, selector: #selector(self.hideToolbarDelayed),
                             userInfo: nil, repeats: false)
      }
    } else {
      let label = navigationController?.toolbar?.viewWithTag(toolbarLabelTag) as? UILabel
      label?.text = "Lexical model successfully downloaded!"
      navigationController?.toolbar?.viewWithTag(toolbarActivityIndicatorTag)?.removeFromSuperview()
      Timer.scheduledTimer(timeInterval: 3.0, target: self, selector: #selector(self.hideToolbarDelayed),
                           userInfo: nil, repeats: false)
      
      view.isUserInteractionEnabled = true
      navigationItem.leftBarButtonItem?.isEnabled = true
      if navigationItem.rightBarButtonItem != nil {
        navigationItem.rightBarButtonItem?.isEnabled = true
      }
      
      // Add lexicalModel.
      for lexicalModel in lexicalModels {
        Manager.shared.addLexicalModel(lexicalModel)
        _ = Manager.shared.registerLexicalModel(lexicalModel)
      }
      
      navigationController?.popToRootViewController(animated: true)
    }
  }
  
  private func lexicalModelDownloadFailed(_ notification: LexicalModelDownloadFailedNotification) {
    view.isUserInteractionEnabled = true
    navigationItem.leftBarButtonItem?.isEnabled = true
    if let item = navigationItem.rightBarButtonItem {
      item.isEnabled = true
    }
    
    let title: String
    if view == navigationController?.topViewController?.view {
      updateQueue = nil
      title = "Lexical model Update Error"
    } else {
      title = "Lexical model Download Error"
    }
    navigationController?.setToolbarHidden(true, animated: true)
    
    let alertController = UIAlertController(title: title, message: notification.error.localizedDescription,
                                            preferredStyle: UIAlertControllerStyle.alert)
    alertController.addAction(UIAlertAction(title: "OK",
                                            style: UIAlertActionStyle.cancel,
                                            handler: nil))
    
    self.present(alertController, animated: true, completion: nil)
  }
  
  private func switchLexicalModel(_ index: Int) {
    // Switch lexicalModel and register to user defaults.
    if Manager.shared.registerLexicalModel(userLexicalModels[index]) {
      tableView.reloadData()
    }
    
    if !_isDoneButtonEnabled {
      Manager.shared.dismissLexicalModelPicker(self)
    }
  }
  
  private func loadUserLexicalModels() {
    userLexicalModels = Storage.active.userDefaults.userLexicalModels ?? []
    tableView.reloadData()
  }
  
  private func isAdded(languageID langID: String, lexicalModelID lmID: String) -> Bool {
    return userLexicalModels.contains { lm in lm.languageID == langID && lm.id == lmID }
  }
  
  @objc func doneClicked(_ sender: Any) {
    Manager.shared.dismissLexicalModelPicker(self)
  }
  
  @objc func cancelClicked(_ sender: Any) {
    Manager.shared.dismissLexicalModelPicker(self)
  }
  
  @objc func addClicked(_ sender: Any) {
    showAddLexicalModel()
  }
  
  @objc func updateClicked(_ sender: Any) {
    navigationController?.toolbar?.viewWithTag(toolbarButtonTag)?.removeFromSuperview()
    let toolbarFrame = navigationController!.toolbar!.frame
    let width = toolbarFrame.width * 0.95
    let height = toolbarFrame.height * 0.7
    let labelFrame = CGRect(x: toolbarFrame.origin.x, y: toolbarFrame.origin.y,
                            width: width, height: height)
    
    let label = UILabel(frame: labelFrame)
    label.backgroundColor = UIColor.clear
    label.textColor = UIColor.white
    label.textAlignment = .center
    label.center = CGPoint(x: width * 0.5, y: height * 0.5)
    label.text = "Updating\u{2026}"
    label.autoresizingMask = [.flexibleLeftMargin, .flexibleRightMargin, .flexibleTopMargin,
                              .flexibleBottomMargin, .flexibleWidth, .flexibleHeight]
    label.tag = toolbarLabelTag
    
    let indicatorView = UIActivityIndicatorView(activityIndicatorStyle: .gray)
    indicatorView.center = CGPoint(x: width - indicatorView.frame.width, y: height * 0.5)
    indicatorView.autoresizingMask = [.flexibleLeftMargin, .flexibleTopMargin, .flexibleBottomMargin]
    indicatorView.tag = toolbarActivityIndicatorTag
    indicatorView.startAnimating()
    navigationController?.toolbar?.addSubview(label)
    navigationController?.toolbar?.addSubview(indicatorView)
    setIsDoneButtonEnabled(true)
    updateLexicalModels()
  }
  
  private func checkUpdates() -> Bool {
    if Manager.shared.apiLexicalModelRepository.lexicalModels == nil {
      return false
    }
    
    isDidUpdateCheck = true
    return userLexicalModels.contains { lexicalModel in
      let lmID = lexicalModel.id
      return Manager.shared.stateForLexicalModel(withID: lmID) == .needsUpdate
    }
  }
  
  private func updateLexicalModels() {
    updateQueue = []
    var lmIDs = Set<String>()
    for lm in userLexicalModels {
      let lmState = Manager.shared.stateForLexicalModel(withID: lm.id)
      if lmState == .needsUpdate {
        if !lmIDs.contains(lm.id) {
          lmIDs.insert(lm.id)
          updateQueue!.append(lm)
        }
      }
    }
    
    if !updateQueue!.isEmpty {
      let langID = updateQueue![0].languageID
      let lmID = updateQueue![0].id
      Manager.shared.downloadLexicalModel(withID: lmID, languageID: langID, isUpdate: true)
    }
  }
  
  private func scroll(toSelectedLexicalModel animated: Bool) {
    let index = userLexicalModels.index { kb in
      return Manager.shared.currentLexicalModelID == kb.fullID
    }
    
    if let index = index {
      let indexPath = IndexPath(row: index, section: 0)
      tableView.scrollToRow(at: indexPath, at: .middle, animated: animated)
      
    }
  }
  
  private func setIsDoneButtonEnabled(_ value: Bool) {
    _isDoneButtonEnabled = value
    if _isDoneButtonEnabled {
      let doneButton = UIBarButtonItem(title: "Done", style: .plain, target: self,
                                       action: #selector(self.doneClicked))
      navigationItem.leftBarButtonItem = doneButton
    } else {
      let cancelButton = UIBarButtonItem(barButtonSystemItem: .cancel, target: self,
                                         action: #selector(self.cancelClicked))
      navigationItem.leftBarButtonItem = cancelButton
    }
  }
  
  @objc func hideToolbarDelayed(_ timer: Timer) {
    navigationController?.setToolbarHidden(true, animated: true)
  }
  
  func showAddLexicalModel() {
    let button: UIButton? = (navigationController?.toolbar?.viewWithTag(toolbarButtonTag) as? UIButton)
    button?.isEnabled = false
    let vc = LanguageViewController(Manager.shared.apiLexicalModelRepository) //may need to be different for models
    navigationController?.pushViewController(vc, animated: true)
    setIsDoneButtonEnabled(true)
  }
}

