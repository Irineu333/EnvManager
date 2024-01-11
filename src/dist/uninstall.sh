#!/usr/bin/env bash

# Check if the script is run as root
if [ "$(id -u)" != "0" ]; then
   echo "Root privileges required to uninstall"
   exit 1
fi

# Identify system
if [ -n "$PREFIX" ] && [ -d "$PREFIX/bin" ]; then
  echo "Uninstalling from Termux on Android"
  INSTALLATION_PATH="$PREFIX" # For Termux on Android
else
  echo "Uninstalling from GNU/Linux"
  INSTALLATION_PATH="/usr/local" # GNU/Linux
fi

# Paths
BIN_PATH="$INSTALLATION_PATH/bin"
LIB_PATH="$INSTALLATION_PATH/lib"

# Remove program files
rm -rf "$LIB_PATH/com.neo.envmanager"

# Remove executable
rm -f "$BIN_PATH/envm"

echo "✔ Uninstalled"