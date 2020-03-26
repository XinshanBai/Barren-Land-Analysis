@ECHO OFF
@CD /D "%~dp0"
CD barren-land-analysis
CALL mvn clean package -DskipTests=true
ECHO.
FOR /F "tokens=*" %%G IN (..\barren_land_analysis_test_cases.txt) DO (
    ECHO Testing Barren Land Analysis with input: %%G
    ECHO|SET /P="Result:"
    ECHO %%G|java -jar target\barren-land-analysis-1.0.0.jar
    ECHO.
    ECHO.
)
PAUSE