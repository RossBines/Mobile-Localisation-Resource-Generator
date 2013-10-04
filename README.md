Mobile-Localisation-Resource-Generator
======================================

Creates localised string resources for iOS and Android from excel spreadsheets

Running the Tool
================

This is just a simple executable jar with some parameters you need to specify

<table>
    <tr>
        <td>-d</td>
        <td>Enable debugging</td>
    </tr>
    <tr>
        <td>-output</td>
        <td>Output location where the generated files will be created</td>
    </tr>
    <tr>
        <td>-input</td>
        <td>Input .xlsx file to read strings data from</td>
    </tr>
    <tr>
        <td>-os</td>
        <td>Specifiy which os's to build, currently only "android" and "ios" are present.</td>
    </tr>
    <tr>
        <td>-all</td>
        <td>Create resources for all os's.</td>
    </tr>
    <tr>
        <td>-input</td>
        <td>Input .xlsx file to read strings data from</td>
    </tr>
    <tr>
        <td>-keyColumnName</td>
        <td>Name of the column in th spreadsheet that acts as the string resource key</td>
    </tr>
    <tr>
        <td>-descriptionColumnName</td>
        <td>Name of the column in th spreadsheet that acts as the string resource description/comment</td>
    </tr>
    <tr>
        <td>-ignoreColumns</td>
        <td>Columns in the table to ignore.  Multiple columns are comma separated such as "Ignore One,Ignore Two,Ignore Three"</td>
    </tr>
    <tr>
        <td>-replacePattern</td>
        <td>A regex pattern used to replace strings that need specific text to be inserted into, such as "Hello my name is {0}".</td>
    </tr>
</table>