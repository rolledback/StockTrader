$javaCompile = "& javac *.java 2>&1";
echo "Compiling..."
$compileOutput = invoke-expression $javaCompile;

If ($compileOutput.length -eq 0)
{
    echo "Compilation complete."
    start-process powershell.exe -argument '-noexit -nologo -noprofile -executionpolicy bypass -command java StockTrader"'
    sleep(1)
    start-process powershell.exe -argument '-noexit -nologo -noprofile -executionpolicy bypass -command java HumanClient"'
    start-process powershell.exe -argument '-noexit -nologo -noprofile -executionpolicy bypass -command java ConsoleClient"'
}
Else
{
    echo "Compilation failed."
    for ($i=0; $i -lt $compileOutput.length; $i++)
    {
        $compileOutput[$i];
    }
}

