// init-twa.cjs — CJS require resolves nested deps, ESM doesn't.
const { execSync } = require('child_process');
const { writeFileSync, readFileSync } = require('fs');

const npmRoot = execSync('npm root -g', { encoding: 'utf8' }).trim();
const { TwaManifest, TwaGenerator } = require(
  npmRoot + '/@bubblewrap/cli/node_modules/@bubblewrap/core'
);

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

new TwaGenerator().createTwaProject('twa', twa).then(() => {
  const xml = readFileSync('twa/app/src/main/res/values/strings.xml', 'utf8')
    .replace(/<string name="appName">[^<]*</, '<string name="appName">信息卡<');
  writeFileSync('twa/app/src/main/res/values/strings.xml', xml);
});
