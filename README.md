Pentaho Data Integration (Kettle) Microsoft® Sharepoint Steps
===================

This repository contains the Pentaho Data Integration (Kettle) steps used to process Microsoft® Sharepoint 2013 lists through the use of the REST API. 


Microsoft and Sharepoint are either registered trademarks or trademarks of Microsoft Corporation in the United States and/or other countries.

Parameters:

Sharepoint Web Site URL:	The URL of the Sharepoint site such as:  https://mysite.com

OData List Endpoint:		The URL of the REST API List such as https://mysite.com/_api/Web/Lists/GetByTitle('My List')
							You can apply OData parameters such as   https://mysite.com/_api/Web/Lists/GetByTitle('My List')?$top=50&$filter= ID gt 50
			
Username:					The username to use for authentication		

Password:					The password to use for authentication	

Domain:						The domain to use (if the server accepts multiple domains). Leave empty if not used.