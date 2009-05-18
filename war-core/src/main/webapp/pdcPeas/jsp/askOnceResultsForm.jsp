<%@ page import="java.util.Locale"%>
<%@ page import="xtrim.data.Domain"%>
<%@ page import="xtrim.data.Result"%>
<%@ include file="checkAdvancedSearch.jsp"%>

<%!
	/**
	 * Checks if given domains is in the array of given domains
	 * @param	selected	list of selected domains
	 * @param	domain		the domain checked
	 * @return	true if "selected" contains "domain".
	 */
	boolean isDomainSelected(String[] selected, String domain)
	{
		for (int i=0; selected!=null && i<selected.length; i++)
		{
			if (domain.equals(selected[i]))
				return true;
		}
		return false;
	}
%>
<%
//r�cup�ration des donn�es pour l'espace de recherche   
   Vector searchDomains = (Vector) request.getAttribute("searchDomains");
   String currentSearchDomainId = (String) request.getAttribute("currentSearchDomainId");
   currentSearchDomainId = (currentSearchDomainId==null) ? "SILVERPEAS" : currentSearchDomainId;

%>

<html>
<head>
<TITLE><%=resource.getString("GML.popupTitle")%></TITLE>
<%
out.println(gef.getLookStyleSheet());
%>
<script type="text/javascript" src="<%=m_context%>/util/javaScript/animation.js"></script>

<SCRIPT Language="Javascript">
	function calculateAction()
	{
		var index = document.queryForm.searchDomainId.selectedIndex;
		var value = document.queryForm.searchDomainId.options[index].value;
		if (value!="SILVERPEAS")
		{
			document.queryForm.action = "SpecificDomainView";
		}
		else
		{
			document.queryForm.action = "GlobalView";
		}
		document.queryForm.submit();
	}
</SCRIPT>
</head>

<body marginheight=5 marginwidth=5 leftmargin=5 topmargin=5 bgcolor="#FFFFFF">

<SCRIPT Language="Javascript">

function openURL(url)
{
	javascript:window.open(url);
	return false;
}

</SCRIPT>

<%
	Frame frameResults=gef.getFrame();

	browseBar.setComponentName(resource.getString("pdcPeas.SearchPage"));

	out.println(window.printBefore());
//	out.flush();

//	getServletConfig().getServletContext().getRequestDispatcher("/pdcPeas/jsp/searchDomainSelection.jsp").include(request, response);

	out.println(frame.printBefore());

	// Retrieve domains available in Ask'Once
	Vector domains = (Vector) request.getAttribute("domains");
	String query = (String) request.getAttribute("query");
	String[] selectedDomains = (String[]) request.getAttribute("selectedDomains");
%>
<center>

<table width="98%" border="0" cellspacing="0" cellpadding="0" class=intfdcolor4><!--tablcontour-->
<tr> 
	<td> 
		<table border="0" cellspacing="0" cellpadding="5" class="contourintfdcolor" width="100%"><!--tabl1-->
		<tr> 
			<td align="center"><!--TABLE SAISIE-->
				<table border="0" cellspacing="0" cellpadding="5" class="intfdcolor4" width="100%">
				<FORM name="queryForm" action="askOnceResultsForm" method="POST">
				<tr>
					<!--<form name="searchDomainChoice" method="POST" action="..." onSubmit="calculateAction()">-->
		<TD class="txtlibform" nowrap><%=resource.getString("pdcPeas.searchDomain")%> :&nbsp;</td>

				<td align="left"> 
							<span class="selectNS"> 
							<select name="searchDomainId" onChange="calculateAction()">
							   <% for (int i=0; searchDomains!=null && i<searchDomains.size() ; i++) 
								  {
									  String[] domain = (String[]) searchDomains.get(i);%>
								   <option <%=currentSearchDomainId.equals(domain[2])?"selected":""%> 	value="<%=domain[2]%>"><%=domain[0]%></option>
								<% } %>
							</select>
							</span>
						</td>
					<!--</form>-->
				</tr>
                <tr>
					<td valign="top" nowrap align="left">
						<span class="txtlibform"><%=resource.getString("pdcPeas.SearchFind")%></span>
					</td>
					<td align="left"> 
						<input type="text" name="query" size="36" value="<%=(query==null) ? "" : query%>">
					</td>
                </tr>
                <tr>
					<td valign="top" nowrap align="left">
						<span class="txtlibform"><%=resource.getString("pdcPeas.askOnce.subDomainSelect")%></span> 
					</td>
					<td align="left"> 
	<table cellpadding="5">
<%
	int i=0;
	for (i=0; domains!= null && i<domains.size(); i++)
	{
		Domain domain = (Domain) domains.get(i);
		if ((i%4)==0)
			out.println("<tr>");
%>
		<td><input type="checkbox" name="domains" value="<%=domain.getInternalName()%>"  <%=isDomainSelected(selectedDomains,domain.getInternalName()) ? "checked":""%>><%=domain.getDisplayName(new Locale("", ""))%></td>
<%
		if ((i%4)==3)
			out.println("</tr>");
	}
		if ((i%4)!=0)
			out.println("</tr>");
%>
	</table>
					</td>
                </tr>
				</FORM>
				</table>
			</td>
		</tr>
		</table>	
	</td>
</tr>
</table>                
</center>
<%
  out.println(frame.printMiddle());
  out.println("<br><CENTER>");

  ButtonPane buttonPane = gef.getButtonPane();
  Button validateButton = (Button) gef.getFormButton(resource.getString("pdcPeas.search"), "javascript:document.queryForm.submit()", false);
  buttonPane.addButton(validateButton);
  buttonPane.setHorizontalPosition();
  out.println(buttonPane.print());

  out.println("</CENTER><br>");

  out.println(frame.printAfter());
%>
<br><br>
<%
	out.println(frameResults.printBefore());
	Vector results = (Vector) request.getAttribute("results");

	if (results!=null)
	{
		String site = "";
		String siteURL = "";
		String title = "";
		String url = "";
		String source = "";
		ArrayPane arrayPane = gef.getArrayPane("askOnceResultsForm", "askOnceResultsForm?reload=false", request, session);

		arrayPane.setTitle(resource.getString("pdcPeas.askOnce.resultTitle") + "	(" + resource.getString("pdcPeas.SearchFind") + query + ")");
		arrayPane.addArrayColumn(resource.getString("pdcPeas.askOnce.Site"));
		arrayPane.addArrayColumn(resource.getString("pdcPeas.askOnce.Title"));
		arrayPane.addArrayColumn(resource.getString("pdcPeas.askOnce.Source"));

		for (int j=0; j<results.size(); j++)
		{
			Result result = (Result) results.get(j);
			site	= (result.getAttribute("site")!=null)	? result.getAttribute("site").getValue()	: "???";
			siteURL	= (site.equals("???"))					? "#"										: "\" onClick=\"javascript:return openURL('http://" + site + "')";
			title	= (result.getAttribute("title")!=null)	? result.getAttribute("title").getValue()	: "???";
			url		= (result.getAttribute("URL")!=null)	? "\" onClick=\"javascript:return openURL('" + result.getAttribute("URL").getValue() + "')" : "#";
			source	= (result.getAttribute("source")!=null) ? result.getAttribute("source").getValue()	: "???";

			ArrayLine ligne = arrayPane.addArrayLine();
			ligne.addArrayCellLink(site, siteURL);
			ligne.addArrayCellLink(title, url);
			ligne.addArrayCellText(source);
		}
		out.println(arrayPane.print());
	}
	else
	{ %>
		<%=resource.getString("pdcPeas.askOnce.Error")%>
<%	}
%>
<%
  out.println(frameResults.printAfter());
  out.println(window.printAfter());
%>
</body>
</html>
