#!/bin/bash

# Script to build ATContentStudio.jar, replicating IntelliJ artifact definition
# Linux and Windows compatible

# --- Platform Detection ---
if [ "$1" = "-windows" ]; then
    echo "Got '-windows' flag. Running Windows version"
    PLATFORM="WINDOWS"
    PATH_SEPARATOR=";" # Windows classpath separator
else
    echo "No '-windows' flag. Running Linux version"
    PLATFORM="LINUX"
    PATH_SEPARATOR=":" # Linux classpath separator
fi

# --- Configuration ---
PACKAGING_DIR=$(dirname "$(readlink -f "$0" || greadlink -f "$0" || stat -f "$0")")
ATCS_SOURCE_DIR=$(dirname "${PACKAGING_DIR}")
TEMP_DIR="${PACKAGING_DIR}/tmp"
JAR_LOCATION="${PACKAGING_DIR}/common/ATCS.jar" # Output JAR location as per script
MANIFEST_LOCATION="${PACKAGING_DIR}/Manifest.txt"
VERSION_FILE="${PACKAGING_DIR}/ATCS_latest"
SOURCE_BASE_DIR="${ATCS_SOURCE_DIR}/src" # Base directory for source code
LIB_BASE_DIR="${ATCS_SOURCE_DIR}/lib"     # Base directory for libraries - ASSUMPTION: Libraries are in project root 'lib'
OUTPUT_JAR_DIR="${PACKAGING_DIR}/common"  # Directory where the final JAR will be placed - as per script

# --- Libraries to include (from IntelliJ artifact definition) ---
LIBRARIES=(
  "AndorsTrainer_v0.1.5.jar"
  "bsh-2.0b4.jar"
  "jide-oss.jar"
  "json_simple-1.1.jar"
  "jsoup-1.10.2.jar"
  "junit-4.10.jar"
  "prefuse.jar"
  "rsyntaxtextarea.jar"
  "ui.jar"
)

# --- Get version ---
echo "Getting version"
VERSION=$(cat "${VERSION_FILE}")
echo "Got version ${VERSION}"

# --- Prepare temporary directory ---
echo "Removing tmp folder"
rm -rf "${TEMP_DIR}"
echo "Recreating tmp folder"
mkdir -p "${TEMP_DIR}/com/gpl/rpg/atcontentstudio" # create package structure in temp dir
mkdir -p "${TEMP_DIR}/lib"

# --- Copy manifest to temp folder for editing ---
echo "Copying manifest to temp folder"
cp "${MANIFEST_LOCATION}" "${TEMP_DIR}"
MANIFEST_LOCATION="${TEMP_DIR}/Manifest.txt" # Update MANIFEST_LOCATION to the temp one

# --- Copy lib files ---
echo 'Copying lib files'
mkdir -p "${TEMP_DIR}/lib/" # Ensure lib dir exists in temp
for LIB in "${LIBRARIES[@]}"; do
    echo "Copying library: ${LIB}"
    cp "${LIB_BASE_DIR}/${LIB}" "${TEMP_DIR}/lib/"
done

# --- Set ClassPath ---
echo 'Setting class path'
CP="lib/*${PATH_SEPARATOR}${SOURCE_BASE_DIR}" # Platform-dependent classpath separator
echo "ClassPath: ${CP}"
echo ""

# --- Build Java classes ---
echo 'Building java classes'
#echo javac -cp "$CP" -sourcepath "${SOURCE_BASE_DIR}" "${SOURCE_BASE_DIR}/com/gpl/rpg/atcontentstudio/*.java" -d "${TEMP_DIR}"
javac -cp "$CP" -sourcepath "${SOURCE_BASE_DIR}" -d "${TEMP_DIR}"

if [ $? -ne 0 ]; then
    echo "Compilation failed. Please check errors above."
    exit 1
fi

# --- Add Class-Path to Manifest ---
echo "Adding Class-Path to Manifest"
LIB_PATHS=$(find "${TEMP_DIR}/lib" -name '*.jar' | paste -sd' ')
echo "LIB_PATHS: ${LIB_PATHS}"
echo "Class-Path: lib/* ${LIB_PATHS}" >>"${MANIFEST_LOCATION}" # Adjusted Class-Path to be relative to JAR

# --- Create JAR file ---
echo ""
echo "Creating jar at location: ${JAR_LOCATION}"

cd "${TEMP_DIR}" || exit # Change to temp dir for JAR command

jar cfm "${OUTPUT_JAR_DIR}/ATCS.jar" "${MANIFEST_LOCATION}"  -C . com/gpl/rpg/atcontentstudio/ lib

if [ $? -ne 0 ]; then
    echo "JAR creation failed."
    exit 1
fi

cd "${PACKAGING_DIR}" || exit # Go back to packaging dir

echo ''
echo "Done creating jar at ${OUTPUT_JAR_DIR}/ATCS.jar"

cp "${OUTPUT_JAR_DIR}/ATCS.jar" "${OUTPUT_JAR_DIR}"

# --- Create archive ---
if [ "$PLATFORM" = "LINUX" ]; then
    cd "${OUTPUT_JAR_DIR}" || exit
    echo "Creating archive"
    tar caf "ATCS_${VERSION}.tar.gz" "common" # archive the 'common' folder which now contains the JAR and libs
    echo "Created archive at ${OUTPUT_JAR_DIR}/ATCS_${VERSION}.tar.gz"
    cd "${PACKAGING_DIR}" || exit
else
  echo "Can't create zip files on windows yet. Please pack the content of the '${OUTPUT_JAR_DIR}/common/' folder yourself"
fi

echo "Script finished."
