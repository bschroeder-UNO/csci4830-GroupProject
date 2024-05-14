package spearmint;

public class GraphicMain {
	public static void main(String[] args) {
        GraphicModel model = new GraphicModel();
        GraphicView view = new GraphicView();
        GraphicController controller = new GraphicController(model, view);
        controller.initialize();
    }
}
