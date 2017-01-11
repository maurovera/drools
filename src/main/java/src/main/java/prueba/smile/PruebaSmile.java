package src.main.java.prueba.smile;

import java.awt.Color;

import smile.Network;
import smile.SMILEException;
import smile.ValueOfInfo;

public class PruebaSmile {

	public void crearRed() {

		try {
			Network net = new Network();

			// Creating node "Success" and setting/adding outcomes:
			// un nodo que representa un evento discreto aleatorio.
			net.addNode(Network.NodeType.Cpt, "Exito");
			// se le agrega dos estados.
			net.setOutcomeId("Exito", 0, "Exito");
			net.setOutcomeId("Exito", 1, "Fallo");

			// Creating node "Forecast" and setting/adding outcomes:
			net.addNode(Network.NodeType.Cpt, "Pronostico");
			net.addOutcome("Pronostico", "Bueno");
			net.addOutcome("Pronostico", "Moderado");
			net.addOutcome("Pronostico", "Pobre");
			net.deleteOutcome("Pronostico", 0);
			net.deleteOutcome("Pronostico", 0);

			// Adding an arc from "Success" to "Forecast":
			net.addArc("Exito", "Pronostico");

			// definicion de las probabilidades apriori del nodo exito
			double[] exitoDef = { 0.2, 0.8 };
			net.setNodeDefinition("Exito", exitoDef);

			// probabilidades a posteriori de pronostico
			double[] pronosticoDef = { 0.4, 0.4, 0.2, 0.1, 0.3, 0.6 };
			net.setNodeDefinition("Pronostico", pronosticoDef);

			// Changing the nodes' spacial and visual attributes:
			net.setNodePosition("Exito", 20, 20, 80, 30);
			net.setNodeBgColor("Exito", Color.red);
			net.setNodeTextColor("Exito", Color.white);
			net.setNodeBorderColor("Exito", Color.black);
			net.setNodeBorderWidth("Exito", 2);
			net.setNodePosition("Pronostico", 30, 100, 60, 30);

			net.writeFile("tutorial_a.xdsl");

		} catch (SMILEException e) {
			System.out.println(e.getMessage());
		}

	}

	// usar la red creada
	public void usarRed() {
		Network net = new Network();
		net.readFile("tutorial_a.xdsl");

		// se hace un udpate del arbol con el algoritmo por
		// defecto. lauritzen

		net.updateBeliefs();

		/*
		 * Obteneremos el resultado pidiendo la probabilidad del resultado
		 * Moderado en el nodo Pronostico. También necesitamos saber cuál es el
		 * índice del estado Moderado en este nodo, es decir, necesitamos la
		 * posición del estado en la matriz de estados del nodo. Lo hacemos
		 * buscando el nombre Moderate en la lista de nombres devueltos por el
		 * método getOutcomeIds ().
		 */

		String[] pronosticoOutcomeIds = net.getOutcomeIds("Pronostico");
		int outcomeIndex;
		for (outcomeIndex = 0; outcomeIndex < pronosticoOutcomeIds.length; outcomeIndex++)
			if ("Moderado".equals(pronosticoOutcomeIds[outcomeIndex]))
				break;

		/*
		 * A continuación, recuperamos los valores del nodo de interés (es
		 * decir, Pronostico), seleccionamos el que corresponde con el índice de
		 * resultado encontrado y lo imprimimos.
		 */

		double[] aValues = net.getNodeValue("Pronostico");
		double P_ForecastIsModerate = aValues[outcomeIndex];
		System.out.println("P(\"Pronostico\" = Moderado) = "
				+ P_ForecastIsModerate);

		// segunda pregunta.
		/*
		 * Respondamos a la segunda pregunta, es decir, ¿cuál es la probabilidad
		 * de fracaso si el pronóstico es bueno? La pregunta también nos da
		 * alguna información: el pronóstico es bueno. Esto significa que antes
		 * de hacer cualquier cálculo, tenemos que introducir esta evidencia en
		 * la red.
		 */
		// se le pasa una evidencia
		net.setEvidence("Pronostico", "Bueno");
		// se actualiza la red
		net.updateBeliefs();

		/*
		 * A continuación, obtendremos la información requerida del nodo Exito,
		 * repitiendo los pasos ya descritos anteriormente.
		 */
		// Getting the index of the "Fallo" outcome:
		String[] aSuccessOutcomeIds = net.getOutcomeIds("Exito");
		for (outcomeIndex = 0; outcomeIndex < aSuccessOutcomeIds.length; outcomeIndex++)
			if ("Fallo".equals(aSuccessOutcomeIds[outcomeIndex]))
				break;

		// Getting the value of the probability:
		aValues = net.getNodeValue("Exito");
		double P_SuccIsFailGivenForeIsGood = aValues[outcomeIndex];
		System.out.println("P(\"Exito\" = Fallo | \"Pronostico\" = Bueno) = "
				+ P_SuccIsFailGivenForeIsGood);

		/*
		 * Ahora la última pregunta, es decir, ¿cuál es la probabilidad de éxito
		 * si el pronóstico es pobre? Como antes, podemos extraer una
		 * información de la pregunta: el pronóstico es pobre. Así que acaba de
		 * introducir esta nueva evidencia en la red y actualizarla de nuevo.
		 */

		net.clearEvidence("Pronostico");
		net.setEvidence("Pronostico", "Pobre");
		net.updateBeliefs();

		/*
		 * Tenga en cuenta que llamamos al método clearEvidence () antes de
		 * establecer la evidencia. En este caso particular no es necesario, ya
		 * que introducimos una nueva evidencia para el mismo nodo. Sin embargo,
		 * si elegimos un nodo diferente, no podemos olvidarnos de aclarar la
		 * evidencia. Ahora estamos listos para obtener el valor de interés, una
		 * vez más, repitiendo los pasos descritos anteriormente.
		 */

		// Getting the index of the "Failure" outcome:
		aSuccessOutcomeIds = net.getOutcomeIds("Exito");
		for (outcomeIndex = 0; outcomeIndex < aSuccessOutcomeIds.length; outcomeIndex++)
			if ("Exito".equals(aSuccessOutcomeIds[outcomeIndex]))
				break;

		// Getting the value of the probability:
		aValues = net.getNodeValue("Exito");
		double P_SuccIsSuccGivenForeIsPoor = aValues[outcomeIndex];

		System.out.println("P(\"Exito\" = Exito | \"Pronostico\" = Pobre) = "
				+ P_SuccIsSuccGivenForeIsPoor);

	}

	/** Actualizar para influir en el diagrama */
	public void actualizarLasInfluenciasDiag() {
		try {
			Network net = new Network();
			net.readFile("tutorial_a.xdsl");

			// Creating node "Invest" and setting/adding outcomes:
			// Crear el nodo "Invertir" y establecer / añadir resultados:
			net.addNode(Network.NodeType.List, "Invertir");
			net.setOutcomeId("Invertir", 0, "Invertir");
			net.setOutcomeId("Invertir", 1, "NoInvertir");

			// Creating the value node "Gain":
			// Creación del nodo de valor "Ganancia":

			net.addNode(Network.NodeType.Table, "Ganancia");

			// Adding an arc from "Invertir" to "Ganancia":
			net.addArc("Invertir", "Ganancia");

			// Adding an arc from "Exito" to "Ganancia":
			net.getNode("Exito");
			net.addArc("Exito", "Ganancia");

			// se rellena las utilidades del nodo ganancia. Las utiidades son:
			// Filling in the utilities for the node "Gain". The utilities are:
			// U("Invest" = Invest, "Success" = Success) = 10000
			// U("Invest" = Invest, "Success" = Failure) = -5000
			// U("Invest" = DoNotInvest, "Success" = Success) = 500
			// U("Invest" = DoNotInvest, "Success" = Failure) = 500
			double[] gananciaDef = { 10000, -5000, 500, 500 };
			net.setNodeDefinition("Ganancia", gananciaDef);

			net.writeFile("tutorial_b.xdsl");
		} catch (SMILEException e) {
			System.out.println(e.getMessage());
		}
	}

	// Inferencia con Diagrama de Influencia
	public void inferenciaConDaigramaInfluencia() {
		try {
			// Loading and updating the influence diagram:
			Network net = new Network();
			net.readFile("tutorial_b.xdsl");
			net.updateBeliefs();

			// Getting the utility node's handle:
			// Obtención del identificador del nodo de utilidad:
			net.getNode("Ganancia");

			// Getting the handle and the name of value indexing parent
			// (decision node):
			// Obtener el identificador y el nombre del elemento de indexación
			// de valor
			// (nodo de decisión):
			int[] aValueIndexingParents = net
					.getValueIndexingParents("Ganancia");
			int nodeDecision = aValueIndexingParents[0];
			String decisionName = net.getNodeName(nodeDecision);

			// Displaying the possible expected values:
			// Visualización de los posibles valores esperados:
			System.out.println("Estas son las utilidades esperadas:");
			for (int i = 0; i < net.getOutcomeCount(nodeDecision); i++) {
				String parentOutcomeId = net.getOutcomeId(nodeDecision, i);
				double expectedUtility = net.getNodeValue("Ganancia")[i];

				System.out.print("  - \"" + decisionName + "\" = "
						+ parentOutcomeId + ": ");
				System.out.println("Utilidad esperada = " + expectedUtility);
			}
		} catch (SMILEException e) {
			System.out.println(e.getMessage());
		}
	}

	public void calcularValorDeLaInformacion() {
		try {
			Network net = new Network();
			net.readFile("tutorial_b.xdsl");

			ValueOfInfo voi = new ValueOfInfo(net);

			// Getting the handles of nodes "Forecast" and "Invest":
			net.getNode("Pronostico");
			net.getNode("Invertir");

			voi.addNode("Pronostico");
			voi.setDecision("Invertir");
			voi.update();

			double[] results = voi.getValues();
			double EVIPronostico = results[0];

			System.out
					.println("Valor Esperado de la informacion (\"Pronostico\") = "
							+ EVIPronostico);
		} catch (SMILEException e) {
			System.out.println(e.getMessage());
		}
	}

}
