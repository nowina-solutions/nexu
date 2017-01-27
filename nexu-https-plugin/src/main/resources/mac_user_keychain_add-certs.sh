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

# Check whether certificate is already installed
security verify-cert -c "$1" -p ssl -L
return_value=$?

# If return code is different from 0, certificate is not (well) installed
if [ $return_value -ne 0 ]
then
  # Add certificate to current user's keychain
  security add-certificate "$1"
  return_value=$?
  if [ $return_value -ne 0 ]
  then
    # Early exit
    exit $return_value
  fi
  
  # Ensure it is trusted for SSL
  security add-trusted-cert -p ssl "$1"
  return_value=$?
  if [ $return_value -ne 0 ]
  then
    # Early exit
    exit $return_value
  fi
fi

# Exit with normal termination status code
exit 0
