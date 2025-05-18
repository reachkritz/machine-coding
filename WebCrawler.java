import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;



public class WebCrawler implements Runnable {
    private static ExecutorService threadPool = Executors.newFixedThreadPool(10);
    private static LinkedBlockingQueue<CrawledUrl> urlQueue = new LinkedBlockingQueue<>();
    private static ConcurrentHashMap<String, Boolean> visited = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, List<String>> relatedUrls = new ConcurrentHashMap<>();
    private static Map<String, String> pages = new HashMap<>(); 
    private static Integer maxDepth = 0;

    @Override
    public void run() {
      while (true) { 
              while (!urlQueue.isEmpty()) {
                  CrawledUrl url = urlQueue.remove();
                  System.out.println("Submitting a page for processing with url "+url.url);
                  threadPool.submit(new Crawler(url));
              }
              try {
              System.out.println("Sleeping for a second.");
              Thread.sleep(1000L);
              } catch (InterruptedException ex) {
                System.out.println("Error occured while sleeping "+ ex.getStackTrace());
              }
      }
    }

    static class CrawledUrl {
        public String url;
        public int depth;
    
        public CrawledUrl(String url, int depth) {
            this.url = url;
            this.depth = depth;
        }
    }

    static class Crawler implements Runnable {
        private final CrawledUrl crawledUrl;

        @Override
        public void run() {
            if (crawledUrl.depth == maxDepth) {
                System.out.println("Maximum depth reached");
                return;
            }

            Parser parser = new Parser();
            List<String> urls = parser.parse(pages.getOrDefault(crawledUrl.url, ""));
            relatedUrls.putIfAbsent(crawledUrl.url, new ArrayList<>());
            relatedUrls.get(crawledUrl.url).addAll(urls);
            printResultMap();

            for (String relatedUrl : urls){
                if (!visited.getOrDefault(relatedUrl, false)) {
                    urlQueue.add(new CrawledUrl(relatedUrl, crawledUrl.depth+1));
                    visited.put(relatedUrl, true);
                }
            }
        }

        public Crawler(CrawledUrl url) {
            this.crawledUrl = url;
        }

    }

    static class Parser {
        public List<String> parse(String pageContent) {
            List<String> list = new ArrayList<>();
            String[] words = pageContent.split(" ");
            Pattern pattern = Pattern.compile("<href=\"[a-zA-Z]+\">", Pattern.CASE_INSENSITIVE);
            for (String word : words) {
                Matcher matcher = pattern.matcher(word);
                if (matcher.find()) {
                    String[] parts = word.split("\"");
                    list.add(parts[1]);
                }
            }
            return list;
        }
    }

    public static void main(String[] args) {
        createTestData();

        WebCrawler webCrawler = new WebCrawler();
        Thread t1 = new Thread(webCrawler);
        urlQueue.add(new CrawledUrl("linkA", 0));
        visited.put("linkA", true);
        maxDepth = 3;

        t1.start();
    }

    static void createTestData() {
        pages.put("linkA", "You can visit <href=\"linkB\"> for more information. <href=\"linkC\"> gives an idea about related topic.");
        pages.put("linkB", "<href=\"linkD\"> photo of the meeting. <href=\"linkB\"> for more info.");
        pages.put("linkC", "On this day, many important events occured. <href=\"linkC\"> and <href=\"linkE\"> for more information.");
        pages.put("linkD", "Just a photo of John Doe <href=\"linkF\"> and his friends.");
    }

    static void printResultMap() {
        System.out.println("### Printing result map starts ###");
        for (Map.Entry<String, List<String>> element : relatedUrls.entrySet()) {
            System.out.println(element.getKey()+ " = "+element.getValue().toString());
        }
        System.out.println("### Printing result map ends ###");
    }
}
