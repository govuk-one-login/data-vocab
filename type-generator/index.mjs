import {compileFromFile} from 'json-schema-to-typescript'
import * as fs from "fs";
import {mkdir, readdir} from "fs/promises";
import * as path from "path";

const args = process.argv.slice(2);
if (args.length < 1) {
    throw new Error("Missing path to JSON schema")
}

async function generate(schemaDir, genDir) {
    const files = (await readdir(schemaDir))
        .filter((f) => f.endsWith(".json"));

    console.log(`${files.length} schema files`, files);

    for (const file of files) {
        const schemaFile = path.join(schemaDir, file);
        console.log(`Generating from ${schemaFile}`);

        const typesFile = path.join(genDir, path.basename(schemaFile) + ".d.ts");
        await compileFromFile(schemaFile)
            .then(ts => fs.writeFileSync(typesFile, ts))
    }

    console.log(`Wrote types files to: ${genDir}`);
}

const schemaDir = args[0];
console.log(`Generating from schema dir: ${schemaDir}`);

const genDir = args.length >= 2 ? args[1] : path.join(process.cwd(), "gen");
if (!fs.existsSync(genDir)) {
    await mkdir(genDir);
}

generate(schemaDir, genDir)
    .catch((cause) => console.error(cause));
