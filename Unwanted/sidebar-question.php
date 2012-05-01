	<div id="sidebar" class="grid_4">
		<ul class="nobullet">
			<li><?php if(function_exists('the_qa_search_form')) : ?>
				<?php the_qa_search_form(); ?>
				<?php else : ?>
				<?php //include (TEMPLATEPATH . '/searchform.php'); ?>
				<?php get_search_form(); ?>
				<?php endif; ?>
			</li>
				
			<?php 	/* Widgetized sidebar, if you have the plugin installed. */
				if ( !function_exists('dynamic_sidebar') || !dynamic_sidebar() ) : ?>
			
			<?php endif; ?>
		</ul>
	</div>

