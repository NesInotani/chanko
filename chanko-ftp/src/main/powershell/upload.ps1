$user     = 'ftpusr1'
$password = $user
$url      = 'ftp://192.168.33.12/'
$src      = 'test.txt'
$dest     = 'test.txt'

$wc = New-Object System.Net.WebClient
$wc.Credentials = New-Object System.Net.NetworkCredential($user,$password)
$wc.BaseAddress = $url
 
$wc.UploadFile($dest, $src)
 
$wc.Dispose()
