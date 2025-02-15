#!/bin/bash

# Script to build ATContentStudio.jar, replicating IntelliJ artifact definition (EXTRACT LIBS)
# Linux and Windows compatible - NO GRADLE - Includes extra source code folders - EXTRACT LIBS

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
SOURCE_BASE_DIR="${ATCS_SOURCE_DIR}/src" # Base directory for standard source code
LIB_BASE_DIR="${ATCS_SOURCE_DIR}/lib"     # Base directory for libraries
OUTPUT_JAR_DIR="${PACKAGING_DIR}/common"  # Directory where the final JAR will be placed - as per script

# --- **ADDITIONAL SOURCE CODE FOLDERS** ---
EXTRA_SOURCE_DIRS=(
  "hacked-libtiled"
  "minify"
  "siphash-zackehh/src/main/java"
)

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
# NO mkdir -p "${TEMP_DIR}/lib" - we are extracting to TEMP_DIR root now

# --- Copy manifest to temp folder for editing ---
echo "Copying manifest to temp folder"
cp "${MANIFEST_LOCATION}" "${TEMP_DIR}"
MANIFEST_LOCATION="${TEMP_DIR}/Manifest.txt" # Update MANIFEST_LOCATION to the temp one

# --- **EXTRACT lib files directly to TEMP_DIR** ---
echo 'Extracting lib files to TEMP_DIR'
for LIB in "${LIBRARIES[@]}"; do
    echo "Extracting library: ${LIB}"
    unzip -o "${LIB_BASE_DIR}/${LIB}" -d "${TEMP_DIR}" # Extract JAR contents to TEMP_DIR root
done

# --- Set ClassPath ---
echo 'Setting class path'
CP="${TEMP_DIR}${PATH_SEPARATOR}${SOURCE_BASE_DIR}" # Classpath is simplified - only source path needed for compilation

# --- **Construct Source Path for Compilation** ---
SOURCE_PATH="${SOURCE_BASE_DIR}" # Start with the standard source path
for EXTRA_SOURCE_DIR in "${EXTRA_SOURCE_DIRS[@]}"; do
  SOURCE_PATH="${SOURCE_PATH}${PATH_SEPARATOR}${ATCS_SOURCE_DIR}/${EXTRA_SOURCE_DIR}" # Add extra source dirs
done

echo "ClassPath: ${CP}"
echo "SourcePath: ${SOURCE_PATH}"
echo ""

# --- Build Java classes ---
echo 'Building java classes'

# Find all java files in source directories and compile them
SOURCE_FILES=$(find "${SOURCE_BASE_DIR}" "${ATCS_SOURCE_DIR}/hacked-libtiled" "${ATCS_SOURCE_DIR}/minify" "${ATCS_SOURCE_DIR}/siphash-zackehh/src/main/java" -name "*.java" -print)

javac -cp "$CP" -sourcepath "${SOURCE_PATH}" -d "${TEMP_DIR}" $SOURCE_FILES
echo "javac -cp \"${CP}\" -sourcepath \"${SOURCE_PATH}\" -d \"${TEMP_DIR}\" $SOURCE_FILES"


if [ $? -ne 0 ]; then
    echo "Compilation failed. Please check errors above."
    exit 1
fi

# --- Copy res stuff to temp folder ---
cp -r "${ATCS_SOURCE_DIR}"/res/* "${TEMP_DIR}/"
mkdir -p "${TEMP_DIR}/com/gpl/rpg/atcontentstudio/img"
mkdir -p "${TEMP_DIR}/tiled/io/resources/"
cp -r "${ATCS_SOURCE_DIR}"/src/com/gpl/rpg/atcontentstudio/img/* "${TEMP_DIR}/com/gpl/rpg/atcontentstudio/img/" # some icons
cp -r "${ATCS_SOURCE_DIR}"/hacked-libtiled/tiled/io/resources/* "${TEMP_DIR}/tiled/io/resources/" # dtd file for tmx maps


# --- Create JAR file ---
echo ""
echo "Creating jar at location: ${JAR_LOCATION}"

cd "${TEMP_DIR}" || exit # Change to temp dir for JAR command

# JAR command WITHOUT lib directory
jar cfm "${OUTPUT_JAR_DIR}/ATCS.jar" "${MANIFEST_LOCATION}"  -C . .

if [ $? -ne 0 ]; then
    echo "JAR creation failed."
    exit 1
fi

cd "${PACKAGING_DIR}" || exit # Go back to packaging dir

echo ''
echo "Done creating jar at ${OUTPUT_JAR_DIR}/ATCS.jar"

# --- Create archive ---
if [ "$PLATFORM" = "LINUX" ]; then
    cd "${OUTPUT_JAR_DIR}" || exit
    echo "Creating archive"
    tar caf "ATCS_${VERSION}.tar.gz" ./* # archive the 'common' folder which now contains the JAR and libs
    echo "Created archive at ${OUTPUT_JAR_DIR}/ATCS_${VERSION}.tar.gz"
    cd "${PACKAGING_DIR}" || exit
else
  echo "Can't create zip files on windows yet. Please pack the content of the '${OUTPUT_JAR_DIR}/common/' folder yourself"
fi

echo "Script finished."
