import * as fs from "fs";
import {mkdir, readdir, writeFile, copyFile} from "fs/promises";
import * as path from "path";

const args = process.argv.slice(2);
if (args.length < 1) {
    throw new Error("Missing path to JSON schema")
}
const schemaDir = args[0];
const genDir = args.length >= 2 ? args[1] : path.join(process.cwd(), "gen");

/**
 * Generate the schema files
 * @param {string} schemaDir The folder containing the original schema files
 * @param {string} genDir The folder to output the index.ts and the transformed schema files
 */
async function generate(schemaDir, genDir) {
    const files = (await readdir(schemaDir))
        .filter((f) => f.endsWith(".json"));

    console.log(`${files.length} schema files`, files);

    let indexFile = path.join(genDir, 'src', 'index.ts');
    let index = [];

    for (const file of files) {
        const schemaFile = path.join(schemaDir, file);
        console.log(`Copying schema ${schemaFile}`);

        const schemaName = path.basename(schemaFile);
        await copyFile(schemaFile, path.join(genDir, 'src', 'schemas', schemaName));
        const schemaExportName = path.basename(schemaName, '.json');

        index.push(`import ${schemaExportName} from'./schemas/${schemaName}';`);
        index.push(`export const ${schemaExportName}Schema = ${schemaExportName};`);
    }
    await writeFile(indexFile, index.join('\n'));

    console.log(`Wrote schemas files to: ${genDir}`);
}

/**
 * Generate source tree
 * @param {string} schemaDir The folder containing the original schema files
 * @param {string} genDir The folder to output the index.ts and the transformed schema files
 */
async function generateSourceTree(schemaDir, genDir) {
    console.log(`Generating from schema dir: ${schemaDir}`);

    if (!fs.existsSync(genDir)) {
        await mkdir(path.join(genDir, 'src', 'schemas'), { recursive: true });
    }

    await generate(schemaDir, genDir);
}

await generateSourceTree(schemaDir, genDir).catch((cause) => console.error(cause));
