param(
    [switch] $console,
    [switch] $serverTest,
    [switch] $stockTest,
    [switch] $scenarioTest
)

$javaCompile = "& javac *.java 2>&1";
echo "Compiling..."
$compileOutput = invoke-expression $javaCompile;

If ($compileOutput.length -ne 0)
{
    echo "Compilation failed."
    for ($i=0; $i -lt $compileOutput.length; $i++)
    {
        $compileOutput[$i];
    }
}
Else
{
    echo "Compilation complete."
    If ($serverTest)
    {
        echo "Starting server."
        start-process powershell.exe -argument '-noexit -nologo -noprofile -executionpolicy bypass -command java StockTrader"'
        sleep(1)
        echo "Starting human client."
        start-process powershell.exe -argument '-noexit -nologo -noprofile -executionpolicy bypass -command java HumanClient"'
        If ($console)
        {
            echo "Starting console."
            start-process powershell.exe -argument '-noexit -nologo -noprofile -executionpolicy bypass -command java ConsoleClient"'
        }
    }
    ElseIf ($stockTest)
    {
        echo "Running stock test."
        java Stock
    }
    ElseIf ($scenarioTest)
    {
        echo "Running scenario test."
        java ScenarioReader
    }
}
