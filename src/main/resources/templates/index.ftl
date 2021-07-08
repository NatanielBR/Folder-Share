<#-- @ftlvariable name="folder" type="java.lang.String" -->
<#-- @ftlvariable name="parent" type="java.lang.String" -->
<#-- @ftlvariable name="files" type="kotlin.collections.List<br.com.nataniel.dto.SimpleFile>" -->
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Index of ${folder}</title>
    </head>
    <body>

        <h2>Index of ${folder}</h2>
        <ul>
            <li><a href="/${parent}">..</a></li>

            <#list files as file>
                <#if file.isDirectory()>
                    <li><a href="/${file.path}">${file.name}/</a></li>
                <#else>
                    <li><a href="/${file.path}">${file.name}</a></li>
                </#if>
            </#list>
        </ul>
    </body>
</html>