const puppeteer = require('puppeteer');

(async () => {
	
	//process.argv.forEach(function (val, index, array) {
	//  console.log(index + ': ' + val);
	//});

	const htmlfile = process.argv[2];
	const pdffile = process.argv[3];
	//const pdffile = htmlfile.replace('html', 'pdf')
	console.log('htmlfile='+htmlfile);
	console.log('pdffile='+pdffile);
	
    const browser = await puppeteer.launch({ headless: 'new' }); // Launch headless Chrome
    const page = await browser.newPage();

    // Load the HTML file
    await page.goto('file://'+htmlfile, { waitUntil: 'networkidle0' });

    // Generate PDF
    await page.pdf({
        path: pdffile, // Output file
        format: 'A4',
        printBackground: true // Ensures CSS backgrounds are printed
    });

    await browser.close();
})();

//node convert_html_to_pdf.js '/mnt/out/temp/Z401614157127_F1/Z401614157127_F1_trial_report.html'
