import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.IOUtils
import org.mortbay.util.ajax.JSON

def createURL(String path) {
    return '../$RESOURCE_PATH/' + path;
}

def createHTML(String url, String type) {
    String entry;

    switch (type) {
        case 'css':
            entry = String.format("<link rel=\"stylesheet\" type=\"text/css\" href=\"%s\"/>", url);
            break;
        case 'js':
            entry = String.format("<script type=\"text/javascript\" src=\"%s\"></script>", url);
            break;
        default:
            entry = "";
    }

    return entry;
}

def change(File dir, def files, def entry) {
    def file = new File(dir, entry.path)
    files.remove(file)

    if (null != entry.order) {
        if ('first'.equalsIgnoreCase(entry.order)) {
            files.add(0, file)
        } else if ('last'.equalsIgnoreCase(entry.order)) {
            files.add(file)
        } else {
            def idx = Integer.parseInt(entry.order)
            files.add(idx, file)
        }
    } else if (null != entry.after) {
        def idx = files.indexOf(new File(dir, entry.after))
        files.add(idx + 1, file)
    } else if (null != entry.before) {
        def idx = files.indexOf(new File(dir, entry.before))
        files.add(idx - 1, file)
    }
}

def aggregate(File compressDir, def files, String extension) {
    def outputFile = new File(compressDir, 'min/' + project.artifactId + '.' + extension)

    if (!outputFile.exists()) {
        FileUtils.touch(outputFile)
    }

    def out = new FileOutputStream(outputFile, true)

    try {
        for (file in files) {
            def input = new FileInputStream(file)
            try {
                IOUtils.copy(input, out)
                out.write('\n'.bytes)
            } catch (Exception e) {
                fail(e.message)
            } finally {
                IOUtils.closeQuietly(input)
            }
        }
    } finally {
        IOUtils.closeQuietly(out)
    }
}

def generate(File compressDir, def files) {
    def headerFile = new File(compressDir, 'header/header.template')

    if (!headerFile.exists()) {
        FileUtils.touch(headerFile)
    }

    def out = new PrintWriter(new FileWriter(headerFile, true))

    try {
        for (file in files) {
            def extenstion = FilenameUtils.getExtension(file.name)
            def url = createURL(file.absolutePath.substring(compressDir.absolutePath.length() + 1))
            def entry = createHTML(url, extenstion)
            out.println(entry)
        }

        out.append("\n")
    } catch (Exception e) {
        fail(e.message)
    } finally {
        IOUtils.closeQuietly(out)
    }
}

def generateMin(File compressDir) {
    def headerFile = new File(compressDir, 'header/header-min.template')

    if (!headerFile.exists()) {
        FileUtils.touch(headerFile)
    }

    def out = new PrintWriter(new FileWriter(headerFile, true))

    try {
        for (file in new File(compressDir, 'min').listFiles()) {
            def extenstion = FilenameUtils.getExtension(file.name)
            def url = createURL(file.absolutePath.substring(compressDir.absolutePath.length() + 1))
            def entry = createHTML(url, extenstion)
            out.println(entry)
        }
    } catch (Exception e) {
        fail(e.message)
    } finally {
        IOUtils.closeQuietly(out)
    }
}

def process(File compressDir, def order, String dirName, String extension) {
    def dir = new File(compressDir, dirName);

    if (dir.exists()) {
        def files = FileUtils.listFiles(dir, [extension].toArray(new String[1]), true)
        files.sort()

        if (null != order) {
            for (entry in order[dirName]) {
                change(dir, files, entry)
            }
        }

        aggregate(compressDir, files, extension)
        generate(compressDir, files)
    }
}

def compressDir = new File(project.properties['yui.compress.dir'])

if (compressDir.exists()) {
    def orderFile = new File(project.basedir, 'src/main/yui-order.js')
    def order = null

    if (orderFile.exists()) {
        log.info('APPLY ORDER FROM: ' + orderFile)
        order = JSON.parse(new FileReader(orderFile))
    } else {
        log.warn('ORDER FILE NOT FOUND. USE ALPHABETICAL ORDER.')
    }

    process(compressDir, order, 'css', 'css')
    process(compressDir, order, 'lib', 'js')
    process(compressDir, order, 'js', 'js')

    generateMin(compressDir)
} else {
    log.warn('YUI COMPRESS DIR NOT EXISTS. NOTHING TO DO.')
}
