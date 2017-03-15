angular
	.module('explorer')
	.service 'ProjectService', [() ->
		projectPromise = null

		getProjectPromise: () ->
			projectPromise

		setProjectPromise: (newProjectPromise) ->
			projectPromise = newProjectPromise
	]