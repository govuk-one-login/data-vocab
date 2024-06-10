import * as fs from "fs";
import {mkdir, readdir, writeFile, copyFile} from "fs/promises";
import * as path from "path";

const args = process.argv.slice(2);
if (args.length < 1) {
    throw new Error("Missing path to JSON schema")
}

async function generate(schemaDir, genDir) {
    const files = (await readdir(schemaDir))
        .filter((f) => f.endsWith(".json"));

    console.log(`${files.length} schema files`, files);

    let indexFile = path.join(genDir, 'src', 'index.ts');
    let index = "";

    for (const file of files) {
        const schemaFile = path.join(schemaDir, file);
        console.log(`Copying schema ${schemaFile}`);

        const schemaName = path.basename(schemaFile);
        await copyFile(schemaFile, path.join(genDir, 'src', 'schemas', schemaName));
        const schemaExportName = path.basename(schemaName, '.json');

        index += `import ${schemaExportName} from'./schemas/${schemaName}';\n`;
        index += `export const ${schemaExportName}Schema = ${schemaExportName};\n`;
    }
    await writeFile(indexFile, index);

    console.log(`Wrote schemas files to: ${genDir}`);
}

const schemaDir = args[0];
console.log(`Generating from schema dir: ${schemaDir}`);

const genDir = args.length >= 2 ? args[1] : path.join(process.cwd(), "gen");
if (!fs.existsSync(genDir)) {
    await mkdir(genDir);
    await mkdir(path.join(genDir, 'src'));
    await mkdir(path.join(genDir, 'src', 'schemas'));
}

generate(schemaDir, genDir)
    .catch((cause) => console.error(cause));
