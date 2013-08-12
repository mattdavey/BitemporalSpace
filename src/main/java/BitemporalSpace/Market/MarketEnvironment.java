package BitemporalSpace.Market;

import org.joda.time.LocalDate;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

// Market environment has to leverage trade date, as we store data each trading data on Mark To Market
// Due to time zones the original idea of <date><data> doesn't feel like it will work
// Can't put trade date down the the URI to far, as it doesn't make sense
// i.e. trying to take a view of all data on x date would possibly be painful

public class MarketEnvironment {
    static Logger logger = Logger.getLogger(MarketEnvironment.class.getName());
    private String location;

    public void delete(String uri) {
        try {
            final URI realUri = new URI(uri);
            delete(realUri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void delete(URI uri) {
        final Node found = find(uri);
        if (found != null)
            found.data = null;
    }

    private class Node {
        final public String name;
        final public HashMap<String, Node> children = new HashMap<String, Node>();
        public java.lang.Object data;

        public Node(final String name) {
            this.name = name;
            logger.info("Creating Node " + name);
        }

        public Node find(final String[] pathComponents, final int nextIndex) {
            final Node found = children.get(pathComponents[nextIndex]);
            if (found == null)
                return found;

//            if (found == null) {
//                for (final Node node : children.values()) {
//                    found = node.find(pathComponents, nextIndex+1);
//                    if (found != null)
//                        break;
//                }
//            }

            if (nextIndex+1 == pathComponents.length)
                return found;
            else
                return found.find(pathComponents, nextIndex+1);
        }

        public void store(final String[] pathComponents, final int nextIndex, final java.lang.Object object) {
            if (nextIndex == pathComponents.length) {
                this.data = object;
                return;
            }

            Node found = children.get(pathComponents[nextIndex]);
            if (found == null)
            {
                found = new Node(pathComponents[nextIndex]);
                children.put(pathComponents[nextIndex], found);
            }

            if (nextIndex+1 == pathComponents.length)
            {
                found.data = object;
                logger.info("Storing data on " + found.name);
            }
            else
                found.store(pathComponents, nextIndex + 1, object);
        }
    }

    final public HashMap<String, Node> rootNodes = new HashMap<String, Node>();
    private LocalDate tradeDate;

    static final private MarketEnvironment instance = new MarketEnvironment();

    public static MarketEnvironment getInstance() {
        return instance;
    }

    public void store(final String uri, final Object object) {
        try {
            final URI realUri = new URI(uri);
            store(realUri, object);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void store(final URI uri, final Object object) {
        final String[] pathComponents = buildPath(uri);
        
        if (pathComponents.length >= 0) {
            Node root = rootNodes.get(pathComponents[0]);
            if (root == null) {
                root = new Node(pathComponents[0]);
                rootNodes.put(pathComponents[0], root);
            }

            if (pathComponents.length > 0)
            {
                root.store(pathComponents, 1, object);
            }
        }
    }

    private String[] buildPath(URI uri) {
        assert location != null : "No Location set on Market Environment";
        assert tradeDate != null: "No trade date set on Market Environment";

        String path = uri.getRawSchemeSpecificPart();
        path = path.substring(3, path.length());
        final String pathComponentsWithoutLocationAndTradeDate[] = path.split("/");
        final String[] pathComponents = new String[pathComponentsWithoutLocationAndTradeDate.length+2];
        System.arraycopy(pathComponentsWithoutLocationAndTradeDate,0, pathComponents, 2, pathComponentsWithoutLocationAndTradeDate.length);
        pathComponents[0] = location;
        pathComponents[1] = tradeDate.toString("ddMMyyyy");
        return pathComponents;
    }

    public void setTradeDate(final LocalDate date) {
        this.tradeDate = date;
    }

    public void setLocation(final String name) {
        this.location = name;
    }

    public java.lang.Object get(final String uri) {
        try {
            final URI realUri = new URI(uri);
            return get(realUri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Node find(final URI uri) {
        final String returnType = uri.getScheme();

        final String[] pathComponents = buildPath(uri);

        if (pathComponents.length > 0) {
            Node found = rootNodes.get(pathComponents[0]);
            if (found != null && pathComponents.length > 1) {
                found = found.find(pathComponents, 1);
            }

            return found;
        }

        return null;
    }
    
    private java.lang.Object get(final URI uri) {
        final Node found = find(uri);
        return (found != null) ? found.data : null;
    }

    public LocalDate getTradeDate() {
        return tradeDate;
    }
}
