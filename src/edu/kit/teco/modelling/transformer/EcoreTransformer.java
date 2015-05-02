package edu.kit.teco.modelling.transformer;

import java.io.File;
import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipselabs.emftriple.sesame.resource.TTLResource;
import org.eclipselabs.emftriple.sesame.resource.TTLResourceFactory;
import org.openrdf.repository.RepositoryException;

public class EcoreTransformer {

	private static ResourceSet rs = new ResourceSetImpl();

	// private static String modelLocation =
	// "file://Users/andreimiclaus/masterarbeit/sources/emftriple/examples/org.eclipselabs.emftriple.examples.basic/model/";

	/**
	 * Check why the ecore extension does not work
	 * http://stackoverflow.com/questions
	 * /9386348/emf-register-ecore-meta-model-programmatically
	 */
	private static void setUpResourceFactories() {
		// due to an apparent bug, the EcoreResourceFactoryImpl is mapped to *
		// because otherwise the model would not load
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("*", new EcoreResourceFactoryImpl());
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ttl", new TTLResourceFactory());
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("rdf", new TTLResourceFactory());
	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws RepositoryException
	 */
	public static void main(String[] args) {
		if (args.length < 4) {
			System.out.println("Ecore Transformer requires meta-model, input model and output URIs\n");
			System.out.println("Usage: java -jar metaModelURI inputModelURI outputModelURI");
			System.out.println("Example java -jar mm.ecore in/model.xmi out/outputmodel.ttl\n");
			System.out
					.println("An attempt will be made to resolve the paths platform independent. If it does not work retry with fully specified absolute paths according to the used platform\n");
			System.out.println("[Current Directory] " + System.getProperty("user.dir"));
		} else {
			System.out.println("Transformation uses dynamic loading of all models\n");
			setUpResourceFactories();
			registerMetaModel(convertToFileURL(args[0]));
			Resource model = readModel(convertToFileURL(args[1]));
			transformToX(model, convertToFileURL(args[2]));
		}

	}

	private static void registerMetaModel(String modelURI) {

		// enable extended metadata
		final ExtendedMetaData extendedMetaData = new BasicExtendedMetaData(rs.getPackageRegistry());
		rs.getLoadOptions().put(XMLResource.OPTION_EXTENDED_META_DATA, extendedMetaData);

		Resource r = rs.getResource(URI.createURI(modelURI), true);
		// EcoreResourceFactoryImpl().createResource(URI.createURI(modelURI));
		EObject eObject = r.getContents().get(0);
		if (eObject instanceof EPackage) {
			EPackage p = (EPackage) eObject;
			// rs.getPackageRegistry().put(p.getNsURI(), p);
			EPackage.Registry.INSTANCE.put(p.getNsURI(), p);
			System.out.println("[Meta Model Loaded] NsURI: " + p.getNsURI() + "\n");
		}

	}

	private static Resource readModel(String inputURI) {
		// Create a resource pointing to the data source
		URI uri = URI.createURI(inputURI);
		Resource resource = Resource.Factory.Registry.INSTANCE.getFactory(uri).createResource(uri);
		// XMIResource resource = new XMIResourceImpl(URI.createURI(inputURI));
		// Load contens of the URI
		try {
			resource.load(null);
		} catch (IOException e) {
			System.out.println("[Error Loading Model]");
			e.printStackTrace();
		}

		System.out.println("[Loaded Model] Contents:" + resource.getContents().get(0) + "\n");
		return resource;
	}

	private static void transformToX(Resource model, String outputURI) {
		// Resource ttlResource = new TTLResource(URI.createURI(file));
		URI uri = URI.createURI(outputURI);
		Resource resource = Resource.Factory.Registry.INSTANCE.getFactory(uri).createResource(uri);

		System.out.println("[Model Contents]" + model.getContents() + "\n");

		resource.getContents().addAll(model.getContents());
		try {
			resource.save(null);
		} catch (IOException e) {
			System.out.println("[Error Saving Model]");
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private static void benchmarkTransformation() throws IOException, RepositoryException {

		System.out.println("Start ...");
		long startTime = System.currentTimeMillis();

		long endTime = System.currentTimeMillis();
		System.out.println("Time to create and store " + (PARENT_COUNT * CHILD_COUNT) + " objects: " + ((endTime - startTime) / 1000.0) + " sec");

	}

	/**
	 * Convert from a filename to a file URL. From SAXLocalNameCount.java from
	 * https://jaxp.java.net
	 */
	private static String convertToFileURL(String filename) {
		// On JDK 1.2 and later, simplify this to:
		// "path = file.toURL().toString()".
		String path = new File(filename).getAbsolutePath();
		if (File.separatorChar != '/') {
			path = path.replace(File.separatorChar, '/');
		}
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		String retVal = "file:" + path;

		return retVal;
	}

	private static final int CHILD_COUNT = 1000;
	private static final int PARENT_COUNT = 5;

}
