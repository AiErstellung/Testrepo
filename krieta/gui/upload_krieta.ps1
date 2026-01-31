<#
.SYNOPSIS
Uploads all JSON files from a folder to the Krieta prefix in the S3 bucket via the AWS CLI.

.DESCRIPTION
Reads credentials and defaults from local.properties (ELMON/APP) and uploads every .json
file to s3://<bucket>/Krieta/. Designed to mirror upload_videos.ps1 without a web server.
#>

param(
    [string]$Folder = "E:\\Videoupload\\Krieta",
    [string]$Bucket,
    [string]$Prefix = "Krieta",
    [string]$Profile = "",
    [string]$Endpoint,
    [string]$Region,
    [string]$LocalProperties = "$PSScriptRoot\\..\\..\\ELMON\\APP\\local.properties"
)

function Read-LocalProperties {
    param([string]$Path)

    $result = [ordered]@{}
    if (-not (Test-Path -LiteralPath $Path)) {
        return $result
    }

    Get-Content -LiteralPath $Path | ForEach-Object {
        $line = $_.Trim()
        if (-not $line -or $line.StartsWith("#")) {
            return
        }

        $separatorIndex = $line.IndexOf("=")
        if ($separatorIndex -lt 0) {
            return
        }

        $key = $line.Substring(0, $separatorIndex).Trim()
        $value = $line.Substring($separatorIndex + 1).Trim()
        $result[$key] = $value
    }

    return $result
}

function Ensure-AwsCli {
    if (-not (Get-Command aws -ErrorAction SilentlyContinue)) {
        throw "Cannot find the AWS CLI in PATH. Install it (https://aws.amazon.com/cli/) and make sure `aws` is reachable."
    }
}

function Invoke-AwsCommand {
    param(
        [string[]]$Arguments,
        [string]$Context
    )

    Write-Host "aws $($Arguments -join ' ')" -ForegroundColor Cyan
    $output = & aws @Arguments 2>&1
    $exit = $LASTEXITCODE
    if ($exit -ne 0) {
        throw "AWS CLI $Context failed (exit $exit):`n$output"
    }

    return $output
}

$localProps = Read-LocalProperties -Path $LocalProperties

$accessKey = $localProps["ELMON_STORAGE_ACCESS_KEY"]
$secretKey = $localProps["ELMON_STORAGE_SECRET_KEY"]
$sessionToken = $localProps["ELMON_STORAGE_SESSION_TOKEN"]

if (-not $accessKey -or -not $secretKey) {
    throw "Access key/secret key missing from $LocalProperties."
}

$env:AWS_ACCESS_KEY_ID = $accessKey
$env:AWS_SECRET_ACCESS_KEY = $secretKey
if ($sessionToken) {
    $env:AWS_SESSION_TOKEN = $sessionToken
}

if (-not $Region) {
    $Region = $localProps["ELMON_STORAGE_REGION"]
}

if (-not $Bucket) {
    $Bucket = $localProps["ELMON_STORAGE_BUCKET"]
}

if (-not $Endpoint) {
    $Endpoint = $localProps["ELMON_STORAGE_ENDPOINT"]
}

if (-not $Bucket) {
    throw "The target bucket is not set. Provide -Bucket or add ELMON_STORAGE_BUCKET to $LocalProperties."
}

if (-not $Endpoint) {
    throw "The endpoint is not set. Provide -Endpoint or add ELMON_STORAGE_ENDPOINT to $LocalProperties."
}

Ensure-AwsCli

$folderPath = Resolve-Path -LiteralPath $Folder -ErrorAction Stop
Write-Host "Uploading JSON from $folderPath to s3://$Bucket/$Prefix"

$jsonFiles = Get-ChildItem -LiteralPath $folderPath -File -Filter *.json | Sort-Object Name

if (-not $jsonFiles) {
    Write-Host "No JSON files found in $folderPath."
    return
}

$endpointArgs = @()
if ($Endpoint) {
    $endpointArgs += "--endpoint-url"
    $endpointArgs += $Endpoint
}

$regionArgs = @()
if ($Region) {
    $regionArgs += "--region"
    $regionArgs += $Region
}

$profileArgs = @()
if ($Profile) {
    $profileArgs = @("--profile", $Profile)
}

foreach ($file in $jsonFiles) {
    $objectKey = "$Prefix/$($file.Name)"
    $destination = "s3://$Bucket/$objectKey"
    $uploadArgs = @(
        "s3", "cp", $file.FullName, $destination,
        "--content-type", "application/json",
        "--acl", "bucket-owner-full-control",
        "--no-progress"
    ) + $profileArgs + $endpointArgs + $regionArgs

    Invoke-AwsCommand -Arguments $uploadArgs -Context "uploading $($file.Name)"
    Write-Host "Uploaded $($file.Name) -> s3://$Bucket/$objectKey"
}
