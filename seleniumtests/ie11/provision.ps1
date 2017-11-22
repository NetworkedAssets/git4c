netsh advfirewall set  allprofiles state off
(iex ((new-object net.webclient).DownloadString('https://chocolatey.org/install.ps1'))) > null.txt
choco install --force -y seleniumiedriver seleniumserver
C:\Windows\System32\cmd.exe /k %windir%\System32\reg.exe ADD HKLM\SOFTWARE\Microsoft\Windows\CurrentVersion\Policies\System /v EnableLUA /t REG_DWORD /d 0 /f