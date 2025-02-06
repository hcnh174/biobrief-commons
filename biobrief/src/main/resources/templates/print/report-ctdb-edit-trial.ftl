<#import "_print.ftl" as patientdb>
<@patientdb.print>
<#list patients as patient>
<div class="break" id="page${patient_index}">
<h2>${i18n['ctdbclinicaltrial']}</h2>
<form>
<div class="cssgrid mode-nested" style="display: grid; grid-gap: 0px; grid-template-columns: 11% 13% 16% 11% 29% 6% 14%">
	<div class="align-left hirolabel" style="grid-area: 1 / 1 / span 1 / span 1">レポート出力</div>
	<div class="align-left hirolabel" style="grid-area: 1 / 2 / span 1 / span 1">該当可能性</div>
	<div class="align-left" style="grid-area: 1 / 3 / span 1 / span 1">TODO</div>
	<div class="align-left hirolabel" style="grid-area: 1 / 4 / span 1 / span 1">Clinical Trial Title_JPN</div>
	<div class="align-left" style="grid-area: 1 / 5 / span 1 / span 1"><span class="value">${model.titleJp!}</span></div>
	<div class="align-left hirolabel" style="grid-area: 1 / 6 / span 1 / span 1">Phase</div>
	<div class="align-left" style="grid-area: 1 / 7 / span 1 / span 1"><span class="value">${model.phase!}</span></div>
	<div class="align-left" style="grid-area: 2 / 1 / span 1 / span 1">TODO</div>
	<div class="align-left hirolabel" style="grid-area: 2 / 2 / span 1 / span 1">確認日</div>
	<div class="align-left" style="grid-area: 2 / 3 / span 1 / span 1"><span class="value"><#if model.confirmationDate??>${model.confirmationDate?string["yyyy/MM/dd"]}</#if></span></div>
	<div class="align-left hirolabel" style="grid-area: 2 / 4 / span 5 / span 1">問合せ情報</div>
	<div class="align-left" style="grid-area: 2 / 5 / span 5 / span 1"><span class="value">${model.inquiryInformation!}</span></div>
	<div class="align-left hirolabel" style="grid-area: 2 / 6 / span 5 / span 1">Mode of Action</div>
	<div class="align-left" style="grid-area: 2 / 7 / span 5 / span 1"><span class="value">${model.modeOfAction!}</span></div>
	<div class="align-left" style="grid-area: 3 / 1 / span 10 / span 1"><span class="value">${model.cancerType?join(', ')}</span></div>
	<div class="align-left hirolabel" style="grid-area: 3 / 2 / span 1 / span 1">Line_max</div>
	<div class="align-left" style="grid-area: 3 / 3 / span 1 / span 1"><span class="value">${model.lineMax!}</span></div>
	<div class="align-left hirolabel" style="grid-area: 4 / 2 / span 1 / span 1">Line_min</div>
	<div class="align-left" style="grid-area: 4 / 3 / span 1 / span 1"><span class="value">${model.lineMin!}</span></div>
	<div class="align-left hirolabel" style="grid-area: 5 / 2 / span 1 / span 1">Age_min</div>
	<div class="align-left" style="grid-area: 5 / 3 / span 1 / span 1"><span class="value">${model.ageMin!}</span></div>
	<div class="align-left hirolabel" style="grid-area: 6 / 2 / span 1 / span 1">Age_max</div>
	<div class="align-left" style="grid-area: 6 / 3 / span 1 / span 1"><span class="value">${model.ageMax!}</span></div>
	<div class="align-left hirolabel" style="grid-area: 7 / 2 / span 1 / span 1">募集状況</div>
	<div class="align-left" style="grid-area: 7 / 3 / span 1 / span 1"><span class="value">${model.recruitmentStatus!}</span></div>
	<div class="align-left hirolabel" style="grid-area: 7 / 4 / span 3 / span 1">適格基準（抜粋）</div>
	<div class="align-left" style="grid-area: 7 / 5 / span 3 / span 3"><span class="value">${model.inclusionCriteria!}</span></div>
	<div class="align-left hirolabel" style="grid-area: 8 / 2 / span 1 / span 1">Alteration</div>
	<div class="align-left" style="grid-area: 8 / 3 / span 1 / span 1"><span class="value">${model.alteration!}</span></div>
	<div class="align-left hirolabel" style="grid-area: 9 / 2 / span 1 / span 1">Source</div>
	<div class="align-left" style="grid-area: 9 / 3 / span 1 / span 1"><span class="value">${model.source!}</span></div>
	<div class="align-left hirolabel" style="grid-area: 10 / 2 / span 1 / span 1">Location</div>
	<div class="align-left" style="grid-area: 10 / 3 / span 1 / span 1"><span class="value">${model.locations!}</span></div>
	<div class="align-left hirolabel" style="grid-area: 10 / 4 / span 2 / span 1">除外基準
	（抜粋）</div>
	<div class="align-left" style="grid-area: 10 / 5 / span 2 / span 3"><span class="value">${model.exclusionCriteria!}</span></div>
	<div class="align-left hirolabel" style="grid-area: 11 / 2 / span 2 / span 1">問い合わせ先</div>
	<div class="align-left" style="grid-area: 11 / 3 / span 2 / span 1"><span class="value">${model.contact!}</span></div>
	<div class="align-left hirolabel" style="grid-area: 12 / 4 / span 1 / span 1">Comment</div>
	<div class="align-left" style="grid-area: 12 / 5 / span 1 / span 3"><span class="value">${model.comment!}</span></div>
</div>
</form>
</div>
</#list>
</@patientdb.print>
