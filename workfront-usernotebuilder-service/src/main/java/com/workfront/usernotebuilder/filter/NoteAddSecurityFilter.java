package com.workfront.usernotebuilder.filter;

import com.attask.event.*;
import com.google.common.base.*;
import com.workfront.usernotebuilder.event.*;

/**
 * Evaluate {@link com.workfront.usernotebuilder.event.AppGUID}s to filter recipients based upon their
 * access level restrictions against the referenced object for the event.
 */
public class NoteAddSecurityFilter extends AbstractEventFilter<AppGUID, NoteAddEvent> {
	public NoteAddSecurityFilter() {}

	public NoteAddSecurityFilter(Filter<AppGUID> anotherFilter) {
		super(anotherFilter);
	}

	@Override
	public Predicate<AppGUID> getBasePredicate(Optional<NoteAddEvent> noteAddEventOptional) {
		return Predicates.notNull();
	}

	/** Filter Check
	 * Is user admin?
	 * __Has admin access level?
	 * __Or is Asproot?
	 * else
	 * __If note isPrivate
	 * ____is user in the same company as note owner?
	 * __If note has view access restriction
	 * ____is user a directed user?
	 * __If note is not being viewed
	 * ____is user owner of note?
	 * ____if note is being deleted
	 * ______is deleting notes enabled?
	 * __does note have global access?
	 * __does user have overall permissions?
	 *
	 */


	// --------------------------------------------------------
	// Check user if the session is not public and if the user is an admin or if it is an asproot
	//
	// if (hasAdminAccess(bizContext)) {
	//	return true;
	//}
	// --------------------------------------------------------

	// --------------------------------------------------------
	// Private notes can only be viewed by admins or those in the same company.
	//
	// if (note.getIsPrivate() && note.getOwnerID() != null &&
	//	!StringUtils.stringsEqual(bizContext.getUser().getCompanyID(), note.getOwner().getCompanyID())) {
	//	return false;
	//}
	// --------------------------------------------------------

	// --------------------------------------------------------
	// Check if there is a restriction on access level to view only notes from threads directed to them
	//
	// if (action == ActionTypeEnum.VIEW && bizContext.hasAccessRestriction(AccessRestrictionTypeEnum.UPDATES_ACCESS) && !isDirectedToUser(note, bizContext.getUser())) {
	//	return false;
	//}
	// --------------------------------------------------------

	// --------------------------------------------------------
	// For anything other than view, you must be the owner of the note, or an admin.
	//
	//if (action != ActionTypeEnum.VIEW && !StringUtils.stringsEqual(bizContext.getUserID(), note.getOwnerID())) {
	//	return false;
	//}
	// --------------------------------------------------------

	// --------------------------------------------------------
	// Check if we are restricting deleting of notes
	//
	//if (action == ActionTypeEnum.DELETE && isPitBossEnabled(FeatureEnum.RestrictNoteDelete)
	//	&& bizContext.hasAccessRestriction(AccessRestrictionTypeEnum.NEVER_DELETE_NOTES)) {
	//	return false;
	//}
	// --------------------------------------------------------

	// --------------------------------------------------------
	// Check for app global access.
	//
	// if (isAppGlobalObject(object) && action != ActionTypeEnum.VIEW) {
	//	return false;
	//}
	// --------------------------------------------------------

	// --------------------------------------------------------
	// Check their access level for overall permissions.
	// if (!hasAccessLevelPermission(ctxt, action)) {
	//	return false;
	//}
	// --------------------------------------------------------

	// --------------------------------------------------------
	// Check if inherited access rules should be excluded
	// if (excludeInheritedRules(ctxt)) {
	//	return false;
	//}
	// --------------------------------------------------------

	// --------------------------------------------------------
	// Their access level doesn't forbid access, but we still need to check the root object to see if they have permission.
	//final SharableSecurityObject root = getSecurityRoot(ctxt, object);
	//if (root == null) {
	//	return true;
	//}
	// --------------------------------------------------------

	//// Determine what action are required on the root object for the inherited action to succeed.
	//final ActionTypeEnum rootAction = translateToRootObjectPermission(action, root);
	//
	//// Forward the access check along to the root object's security filter.
	//final SecurityFilter rootSecurityFilter = new LucidSecurityModel().getSecurityFilter(root.getClass());
	//
	//// When recursing to the root object make sure we ignore the access level permissions checks.  For example, we don't want to deny access to a
	//// task just because they don't have view access on projects.  They should be able to inherit access to tasks no matter what project is set to.
	//return runWithoutAccessLevelPermissions(ctxt, new AtTaskRunnable<Boolean>() {
	//	@Override
	//	public Boolean run() throws AtTaskException {
	//		return rootSecurityFilter.hasAccess(ctxt, root, rootAction);
	//	}
	//});
}
