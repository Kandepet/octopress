<?php

/*
Template Name: AllPages
*/

$file = dirname( __FILE__ );
require( $file . '/../../../wp-load.php' );

get_header(); ?>





        <div id="main">
            <div id="content">

            <div class="blog-index">
                <article class="hentry">
                    <header>
                    <h1 class="entry-title"><a href="<?php the_permalink() ?>"><?php the_title(); ?></a></h1>
                    </header>

				<!--<div class="entry">-->
                <div class="entry-content">
    <?php simpleYearlyArchive(); ?>
				</div>
                    </article>

			</div>

    <aside role=sidebar> <?php get_sidebar(); ?> </aside>

	</div>
</div>











<?php //get_sidebar(); ?>

<?php get_footer(); ?>
