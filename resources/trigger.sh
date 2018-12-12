#!/bin/bash
#----------------------------------------------------------------------------
# Detect list of files changed by a PR and trigger the appropriate CI builds
# param1 - branch name
#----------------------------------------------------------------------------

#
# This script is uses commands available to Git Bash on Windows. (mingw32)

display_usage() {
  echo "Usage: $0 <branch> <PR>"
  echo "branch: String of the associated Git branch on the PR"
  echo "PR:     Number associated with https://github.com/keymanapp/keyman/pulls"
  exit 1
}

fail() {
    FAILURE_MSG="$1"
    if [[ "$FAILURE_MSG" == "" ]]; then
        FAILURE_MSG="Unknown failure"
    fi
    echo "${ERROR_RED}$FAILURE_MSG${NORMAL}"
    exit 1
}

function validate_params() {
  if [[ "$#" -ne "2" ]] || ! [[ "$2" =~ ^[0-9]+$ ]]; then
    display_usage
  fi
}

setup() {
  echo "Checking triggers for branch: $1 - https://github.com/keymanapp/keyman/pulls/$2"
  git fetch
  git checkout master
  git pull
  git checkout $1
  git pull
}

get_diffs() {
  DO_ANDROID=false
  DO_IOS=false
  DO_KEYBOARDPROCESSOR=false
  DO_LINUX=false
  DO_MAC=false
  DO_WEB=false
  DO_WINDOWS=false
  
  mapfile -t < <( git diff --numstat master... )
  # echo "mapfile: ${MAPFILE[@]}"

  shopt -s nocasematch
  for row in ${MAPFILE[@]}; do
    # echo "row: $row"
    for column in $row; do
      # search on pathnames (ignore line numbers and history.md files)
      if [[ $column =~ ^[0-9]+$ ]] || [[ $column =~ history.md ]]; then
        continue;
      fi

      path="$column"
      echo "Change in $path"

      case "$path" in
      android*)
          DO_ANDROID=true;;
      ios*)
          DO_IOS=true;;
      common/engine/keyboardprocessor*)
          DO_KEYBOARDPROCESSOR=true;;
      linux*)
          DO_LINUX=true;;
      mac*)
          DO_MAC=true;;
      web*)
          DO_WEB=true;;
      windows*)
          DO_WINDOWS=true;;
      esac
    done
  done

  echo "DO_ANDROID: $DO_ANDROID"
  echo "DO_IOS: $DO_IOS"
  echo "DO_KPAPI: $DO_KEYBOARDPROCESSOR"
  echo "DO_LINUX: $DO_LINUX"
  echo "DO_MAC: $DO_MAC"
  echo "DO_WEB: $DO_WEB"
  echo "DO_WINDOWS: $DO_WINDOWS"


}

validate_params "$@"
setup "$@"
get_diffs "$@"
