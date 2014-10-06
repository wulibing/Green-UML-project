package edu.buffalo.cse.greenest;

import java.util.HashSet;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import edu.buffalo.cse.greenest.model.TypeModel;
import edu.buffalo.cse.greenest.model.ViewModel;
import edu.buffalo.cse.greenest.view.TypeView;

public abstract  class Activator implements BundleActivator {
	private HashSet<TypeModel> _typeModel;
    private HashSet<ViewModel> _typeViewModel;
    private HashSet<TypeView> _typeView;
	private static BundleContext context;

	

	static BundleContext getContext() {
	
		
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		_typeModel= new HashSet<TypeModel>();// create an empty HashSet collection
		_typeViewModel=new HashSet<ViewModel>();
		_typeView=new HashSet<TypeView>();
		Activator.context = bundleContext;
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}
	
	public void addTypeModel(TypeModel t){
		_typeModel.add(t);
	}
	
	public void addTypeViewModel(ViewModel t){
		_typeViewModel.add(t);
	}
	
	public void addTypeView(TypeView t){
		_typeView.add(t);
	}
}
