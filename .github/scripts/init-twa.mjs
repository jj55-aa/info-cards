// Init TWA project via @bubblewrap/core Node API. bubblewrap init is interactive — pipes fail.
import { execSync } from 'child_process';
import { writeFileSync, readFileSync } from 'fs';
import { join } from 'path';

// @bubblewrap/core is a nested dep of @bubblewrap/cli — resolve from global install
const npmGlobalRoot = execSync('npm root -g', { encoding: 'utf8' }).trim();
const coreDir = join(npmGlobalRoot, '@bubblewrap', 'cli', 'node_modules', '@bubblewrap', 'core');
const { TwaManifest, TwaGenerator } = await import(coreDir);

const twa = new TwaManifest({
  packageId: 'io.github.jj55_aa.info_cards',
  name: '工作信息卡',
  launcherName: '信息卡',
  display: 'standalone',
  backgroundColor: '#0f0f0f',
  themeColor: '#0f0f0f',
  startUrl: '/info-cards/info-cards.html',
  host: 'jj55-aa.github.io',
  webManifestUrl: 'https://jj55-aa.github.io/info-cards/manifest.json',
  iconUrl: 'https://jj55-aa.github.io/info-cards/icon-512.png',
  maskableIconUrl: 'https://jj55-aa.github.io/info-cards/icon-512.png',
  signingKey: { path: './debug.keystore', alias: 'androiddebugkey', password: 'android' },
});

execSync(
  'keytool -genkey -v -keystore debug.keystore -storepass android -alias androiddebugkey -keypass android -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=Debug,O=Android,C=US"',
  { stdio: 'inherit' }
);

await new TwaGenerator().createTwaProject('twa', twa);

const xml = readFileSync('twa/app/src/main/res/values/strings.xml', 'utf8')
  .replace(/<string name="appName">[^<]*</, '<string name="appName">信息卡<');
writeFileSync('twa/app/src/main/res/values/strings.xml', xml);
