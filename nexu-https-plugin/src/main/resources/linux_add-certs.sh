#!/bin/bash

# © Nowina Solutions, 2015-2017
#
# Concédée sous licence EUPL, version 1.1 ou – dès leur approbation par la Commission européenne - versions ultérieures de l’EUPL (la «Licence»).
# Vous ne pouvez utiliser la présente œuvre que conformément à la Licence.
# Vous pouvez obtenir une copie de la Licence à l’adresse suivante:
#
# http://ec.europa.eu/idabc/eupl5
#
# Sauf obligation légale ou contractuelle écrite, le logiciel distribué sous la Licence est distribué «en l’état»,
# SANS GARANTIES OU CONDITIONS QUELLES QU’ELLES SOIENT, expresses ou implicites.
# Consultez la Licence pour les autorisations et les restrictions linguistiques spécifiques relevant de la Licence.

# Add certificate to Firefox
# ==========================
# Script constant
USER_PROFILES_ROOT_DIRECTORY="$HOME/.mozilla/firefox"

# If user profiles root directory exists, add certificate
if [ -d "$USER_PROFILES_ROOT_DIRECTORY" ]
then
  for profile_directory in "$USER_PROFILES_ROOT_DIRECTORY"/*
  do
    if [ -d "$profile_directory" ] && [ "$profile_directory" != "$USER_PROFILES_ROOT_DIRECTORY/Crash Reports" ]
    then
      certutil -A -n "$1-localhost" -i "$2" -t "cTC,cTC,cTC", -d "$profile_directory"
      return_value=$?
      if [ $return_value -ne 0 ]
      then
        # Exit with error code
        exit $return_value
      fi

      certutil -A -n "$1-localhost" -i "$2" -t "cTC,cTC,cTC", -d sql:"$profile_directory"
      return_value=$?
      if [ $return_value -ne 0 ]
      then
        # Exit with error code
        exit $return_value
      fi
    fi
  done
fi


# Add certificate to Chrome
# =========================
# Script constant
CHROME_NSS_DB_ROOT_DIRECTORY="$HOME/.pki/nssdb"

# If Chrome NSS DB root directory exists, add certificate
if [ -d "$CHROME_NSS_DB_ROOT_DIRECTORY" ]
then
  certutil -A -n "$1-localhost" -i "$2" -t "cTC,cTC,cTC", -d sql:"$CHROME_NSS_DB_ROOT_DIRECTORY"
  return_value=$?
  if [ $return_value -ne 0 ]
  then
    # Exit with error code
    exit $return_value
  fi
fi

# Exit with normal termination status code
exit 0
