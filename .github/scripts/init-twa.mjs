// Init TWA project via @bubblewrap/core Node API
// bubblewrap init CLI is interactive (inquirer.js reads stdin char-by-char) — pipes ALWAYS fail.
import { TwaManifest, TwaGenerator } from '@bubblewrap/core';
import { createHash } from 'crypto';
import { writeFileSync, readFileSync } from 'fs';

const pkg = 'io.github.jj55_aa.info_cards';
const baseUrl = 'https://jj55-aa.github.io';

const twa = new TwaManifest({
  packageId: pkg,
  name: '工作信息卡',
  launcherName: '信息卡',
  display: 'standalone',
  backgroundColor: '#0f0f0f',
  themeColor: '#0f0f0f',
  startUrl: '/info-cards/info-cards.html',
  host: 'jj55-aa.github.io',
  webManifestUrl: `${baseUrl}/info-cards/manifest.json`,
  iconUrl: `${baseUrl}/info-cards/icon-512.png`,
  maskableIconUrl: `${baseUrl}/info-cards/icon-512.png`,
  // debug keystore with fixed password
  signingKey: {
    path: './debug.keystore',
    alias: 'androiddebugkey',
    password: 'android',
  },
});

// Debug keystore creation
import { execSync } from 'child_process';
execSync(
  `keytool -genkey -v -keystore debug.keystore -storepass android -alias androiddebugkey -keypass android -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=Debug,O=Android,C=US"`,
  { stdio: 'inherit' }
);

await new TwaGenerator().createTwaProject('twa', twa);

// Write app name to strings.xml so the app name shows correctly
const stringsPath = 'twa/app/src/main/res/values/strings.xml';
const stringsXml = readFileSync(stringsPath, 'utf8')
  .replace(/<string name="appName">[^<]*</, '<string name="appName">信息卡<');
writeFileSync(stringsPath, stringsXml);
