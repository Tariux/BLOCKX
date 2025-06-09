const { exec } = require('child_process');
const fs = require('fs');
const path = require('path');


const gradleBuildCommand = 'gradle build'; 
const rootProjectPath = 'C:/Users/Tariux/Desktop/block/blockx'; 
const jarFilePath = path.join(rootProjectPath, 'build/libs/blockx-1.0.jar'); 
const destinationFolder = 'C:/Users/Tariux/Desktop/Git/paper/temp/the-debug/plugins'; 


function runGradleBuild() {
    return new Promise((resolve, reject) => {
        exec(gradleBuildCommand, { cwd: rootProjectPath }, (error, stdout, stderr) => {
            if (error) {
                reject(`Error: ${error.message}`);
                return;
            }
            if (stderr) {
                
                
            }
            console.log(stdout);
            resolve();
        });
    });
}


function moveJarFile() {
    return new Promise((resolve, reject) => {
        if (fs.existsSync(jarFilePath)) {
            const destinationPath = path.join(destinationFolder, 'blockx-1.0.jar');
            fs.rename(jarFilePath, destinationPath, (err) => {
                if (err) {
                    reject(`📛 Error moving file: ${err}`);
                    return;
                }
                console.log(`✔ JAR file moved to: ${destinationPath}`);
                resolve();
            });
        } else {
            reject('📛 JAR file not found.');
        }
    });
}


async function buildAndMoveJar() {
    try {
        console.log('Running Gradle build...');
        await runGradleBuild();
        console.log('Build complete. Moving JAR file...');
        await moveJarFile();
        console.log('Operation completed successfully.');
    } catch (error) {
        console.error('Error:', error);
    }
}


buildAndMoveJar();
