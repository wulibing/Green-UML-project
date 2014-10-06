package edu.buffalo.cse.green.editor.controller;

public enum PropertyChange {
	Children(), Element(), Type(), Name(),
	DisposeIcon(), Location(), Redraw(), Refresh(), Size(), Visibility(),
	Note(),
	RelationshipBendpoint(), RelationshipCardinality(),
	RelationshipSource(), RelationshipTarget(),
	IncomingRelationship(), OutgoingRelationship(),
	GenerateRelationship(), RemoveRelationship(), UpdateRelationships();
}
