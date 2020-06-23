#Latex-Renderer
Service for rendering latex content such as algorithms, diagrams, pictures etc. Latex-Render accepts the document body, the packages that are required to render the document and offers svg, png and pdf as output formats.

###Version:
The latest version is: **0.1**

###Requirements:
Runs on: Windows
Port: 8081

3rd party software:
  * Full latex distribution (for example miktex)
  * For SVG support: Installation of Poppler for Windows and Path setup of pdf2svg2.bat  https://community.jalios.com/jcms/jc2_183627/en/pdf2svg2-bat-script
  * For PNG support: Ghostscript 
	* The directory containing gswin32c.exe shall be added to PATH.
	* ImageMagick (including legacy scripts) - rename convert.exe to imgconvert.exe 

###Installation:
  1. Download Latex-Renderer
  2. Make sure all required programms are installed.
  3. 1st time Setup: mvn package -DskipTests 
  4. To start Latex-Renderer run: mvn spring-boot:run


###API:
http://localhost:8081/renderLatex  expects (String content, List<String> latexPackages, String output) as JSON

Export formats are: pdf, png, fullPdf, svg, svgz

Example:
{"content":"\\begin{quantikz}\n\t\\lstick{$\\ket{0}$} & \\gate{H} & \\ctrl{1} & \\gate{U} & \\ctrl{1} & \\swap{2} & \\ctrl{1} & \\qw \\\\\n\t\\lstick{$\\ket{0}$} & \\gate{D} & \\targ{} & \\octrl{-1} & \\control{} & \\qw & \\octrl{1} & \\qw \\\\\n\t&&&&&\\targX{} & \\gate{F} & \\qw\n\\end{quantikz}", 
"latexPackages":["\\usepackage{tikz}","\\usetikzlibrary{quantikz}"], "output":"svg"}




Upcoming versions shall include:
unix compatiblity, easier setup, shipment as container