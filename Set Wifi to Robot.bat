netsh interface ipv4 set address name="Wi-Fi" source=static address=10.14.58.73 mask=255.255.255.0 gateway=10.14.58.1
pause
netsh wlan connect name="1458"
pause