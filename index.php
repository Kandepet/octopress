
<?php get_header(); ?>
        <div id="main">
            <div id="content">

            <div class="blog-index">
	<?php if (have_posts()) : ?>

		<?php while (have_posts()) : the_post(); ?>

                <article class="hentry">
                    <header>
                    <h1 class="entry-title"><a href="<?php the_permalink() ?>"><?php the_title(); ?></a></h1>
                    <p class="meta">
                    <time datetime="2011-07-30T13:34:00-05:00" pubdate  data-updated="true" ><?php the_time('d') ?>, <?php the_time('M y') ?></time></p>
                    </header>

				    <!--<div class="entry">-->
                    <div class="entry-content">
					    <!--<?php the_content('<em>Continue reading &rarr;</em>'); ?>-->
					    <?php the_excerpt(); ?>
                    </div>
                </article> 
				<div class="clearfix"></div>


		<?php endwhile; ?>

	<?php else : ?>

		<h2 class="center">Not Found</h2>
		<p class="center">Sorry, but you are looking for something that isn't here.</p>
		<?php //get_search_form(); ?>

	<?php endif; ?>
			</div>

             <aside role=sidebar> <?php get_sidebar(); ?> </aside>

                         </div>
                     </div>


<?php get_footer(); ?>

