# Testing

## Requirements

### Windows
- [Vagrant](https://www.vagrantup.com/downloads.html)
- [TightVNC](https://www.tightvnc.com/download.php) (only viewer - selected during installation)

### Linux
- Docker
- Docker-compose

### Mac
Same as Windows

## Setup

### Windows
- Add `127.0.0.1 localconfluence` to hosts file (`C:\Windows\System32\drivers\etc\hosts`)
- Run `startmachine.cmd` in one terminal
- Run `initialsetup.cmd` in second terminal
- After docker-compose started run `runtests.cmd` in second terminal

### Linux
TODO

### Mac
TODO

### Initial setup
- To setup Confluence run `SetupConfluence` and then `InstallPlugin` tests

### Notes
Host IP in Vagrant VM is always `10.0.2.2`

## Development workflow

### Setup

#### Windows

- Install Chrome browser and Firefox
- Download [Chrome driver](https://sites.google.com/a/chromium.org/chromedriver/downloads) and extract it to `C:\Selenium\chromedriver.exe`
- Download [Gecko driver](https://github.com/mozilla/geckodriver/releases) and extract it to `C:\Selenium\geckodriver.exe`

#### Linux (TODO)

#### Mac (TODO)

### Local testing setup
- 2 IntelliJ IDEA instances in 2 separate Git4C source directories (one for plugin code, second one for test code)
- `atlas-run` in first instance (this is where code will be edited)
- DON'T use `atlas` in second instance (this one will be for code editing). Because we don't use `atlas` command on this instance we can invoke tests from IDE

## Environmental variables
- FIREFOX_LOCATION: ip:port of firefox instance or location of gecko driver
- CONFLUENCE_LOCATION: url of Confluence location to test

## Confluence settings
- Confluence must have anonymous access enabled