param(
    [switch] $console,
    [switch] $server,
    [switch] $stock
    # [switch] $scenario
)

If ($server)
{
    echo "Starting server.`n"
    start-process powershell.exe -argument '-noexit -nologo -noprofile -executionpolicy bypass -command java -jar .\server\build\libs\StockTrader.jar"'
    sleep(1)
    echo "Starting human client.`n"
    start-process powershell.exe -argument '-noexit -nologo -noprofile -executionpolicy bypass -command java -jar .\client\build\libs\HumanClient.jar"'
    If ($console)
    {
        echo "Starting console.`n"
        start-process powershell.exe -argument '-noexit -nologo -noprofile -executionpolicy bypass -command java -jar .\console\build\libs\ConsoleClient.jar"'
    }
}
ElseIf ($stock)
{
    echo "Running stock test.`n"
    java -jar .\server\build\libs\Stock.jar
}
ElseIf ($scenario)
{
    echo "Running scenario test.`n"
    java .\build\libs\ScenarioReader.jar
}
