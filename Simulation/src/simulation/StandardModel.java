package simulation;

public class StandardModel extends BaseModel implements IModel {

	/**
	 * Foo.
	 */
	private ModelElement _modelElement2;
	/**
	 * Foo.
	 */
	private ModelElement _modelElement;
	private IView _viewer;

	public StandardModel(IView viewer) {
		_viewer = viewer;
		_modelElement2 = new ModelElement();
		_modelElement = new ModelElement();
	}

	public void updateViews() {
		_viewer.update();
		new Helper();
	}

	@Override
	public void addElement(ModelElement element) {
		// TODO Auto-generated method stub
		super.addElement(element);
		_modelElement.toString();
		_modelElement2.toString();
	}
}
