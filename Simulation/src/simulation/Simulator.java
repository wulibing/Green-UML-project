package simulation;

public class Simulator {

	/**
	 * 
	 */
	private IView _view;
	/**
	 * 
	 */
	private IModel _model;

	public Simulator() {
		_view = new RadarView();
		_model = new StandardModel(_view);
		_model.toString();
	}

}
