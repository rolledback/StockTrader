param(
    [switch] $console,
    [switch] $serverTest,
    [switch] $stockTest,
    [switch] $compile
)

If ($compile)
{
    $javaCompile = "& javac *.java 2>&1";
    echo "Compiling..."
    invoke-expression $javaCompile;
}
ElseIf ($serverTest)
{
    $javaCompile = "& javac *.java 2>&1";
    echo "Compiling..."
    $compileOutput = invoke-expression $javaCompile;
    echo "Compilation complete."

    If ($compileOutput.length -eq 0)
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
    Else
    {
        echo "Compilation failed."
        for ($i=0; $i -lt $compileOutput.length; $i++)
        {
            $compileOutput[$i];
        }
    }    
}
ElseIf ($stockTest)
{
    $javaCompile = "& javac *.java 2>&1";
    echo "Compiling..."
    $compileOutput = invoke-expression $javaCompile;
    echo "Compilation complete."

    If ($compileOutput.length -eq 0)
    {
        echo "Running stock test."
        java Stock
    }
    Else
    {
        echo "Compilation failed."
        for ($i=0; $i -lt $compileOutput.length; $i++)
        {
            $compileOutput[$i];
        }
    } 
}
